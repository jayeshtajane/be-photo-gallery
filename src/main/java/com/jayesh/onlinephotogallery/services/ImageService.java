package com.jayesh.onlinephotogallery.services;

import com.google.common.io.Files;
import com.jayesh.onlinephotogallery.dto.ImageDTO;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.repositories.es.ImageRepositoryEs;
import com.jayesh.onlinephotogallery.util.AppUtils;
import com.jayesh.onlinephotogallery.util.CloudinaryUtils;
import com.jayesh.onlinephotogallery.util.ListResponse;
import com.jayesh.onlinephotogallery.util.exceptions.ImageNotFoundException;
import com.jayesh.onlinephotogallery.util.exceptions.ImageStorageException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.util.*;

@Service
public class ImageService {

    @Autowired
    private ImageRepositoryEs imageRepositoryEs;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private CloudinaryUtils cloudinaryUtils;

    @Autowired
    private AppUtils appUtils;

    public ImageDocument storeFile(MultipartFile file, UserDocument userDocument) {
        String fileName = file.getOriginalFilename();
        ImageDocument imageDocument;
        try {
            if(fileName.contains("..")) {
                throw new ImageStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Map resultMap = cloudinaryUtils.uploadImage(appUtils.toFile(file, fileName));
            imageDocument = ImageDocument.createDocument(resultMap, userDocument.getId());
        } catch (IOException | ParseException ex) {
            throw new ImageStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
        return imageDocument;
    }

    @Transactional(readOnly = true)
    public ListResponse<ImageDocument> getImages(Integer pageNo, Integer sort, Integer itemsPerPage, String albumName, UserDocument userDocument, boolean fromTrash) {
        List<ImageDocument> imageDocuments;
        if(albumName == null || albumName.isEmpty()) {
            imageDocuments = imageRepositoryEs.findByUserIdAndTrashOrderByCreatedAtDesc(userDocument.getId(), fromTrash);
        }
        else {
            imageDocuments = imageRepositoryEs.findByUserIdAndTrashOrderByCreatedAtDesc(userDocument.getId(), fromTrash);
        }

        ListResponse<ImageDocument> listResponse = new ListResponse<>();
        listResponse.setPageNo(pageNo);
        listResponse.setItemsPerPage(itemsPerPage);
        listResponse.setListData(imageDocuments);
        listResponse.setTotalCount(imageDocuments.size());
        return listResponse;
    }

    public Iterator<ImageDocument> saveImages(List<ImageDocument> imageEntities) {
        return imageRepositoryEs.saveAll(imageEntities).iterator();
    }

    @Transactional
    public Long deleteImage(String imageId, UserDocument userDocument) {
        if(imageRepositoryEs.removeByIdAndUserId(imageId, userDocument.getId()) <= 0) {
            return 0L;
        }
        albumService.removeImageFromAlbums(imageId, userDocument);
        ImageDocument imageDocument = imageRepositoryEs.findByIdAndUserId(imageId, userDocument.getId());
        if(imageDocument != null) {
            try {
                cloudinaryUtils.deleteImage(imageDocument.getPublicId());
            } catch (IOException e) {
                throw new ImageStorageException("Something went wrong", e);
            }
            return 1L;
        }
        throw new ImageNotFoundException("File not found " + imageId);
    }

    @Transactional
    public List<ImageDocument> deleteMultipleImage(List<String> imageIds, UserDocument userDocument) {
        List<ImageDocument> imageDocuments = imageRepositoryEs.removeByIdInAndUserId(imageIds, userDocument.getId());
        imageDocuments.forEach(image -> {
            try {
                albumService.removeImageFromAlbums(image.getId(), userDocument);
                cloudinaryUtils.deleteImage(image.getPublicId());
            } catch (IOException e) {
                throw new ImageStorageException("Something went wrong", e);
            }
        });
        return imageDocuments;
    }

    @Transactional
    public Integer moveMultipleImagesToTrash(List<String> imageIds, UserDocument userEntity) {
        imageIds.forEach(id -> {
            Optional<ImageDocument> imageDocumentOptional = imageRepositoryEs.findById(id);
            if(imageDocumentOptional.isPresent()) {
                ImageDocument imageDocument = imageDocumentOptional.get();
                imageDocument.setTrash(true);
                imageRepositoryEs.save(imageDocument);
            }
        });
        return imageIds.size();
    }

    @Transactional
    public Integer moveImageToTrash(String imageId, UserDocument userDocument) {
        Optional<ImageDocument> imageDocumentOptional = imageRepositoryEs.findById(imageId);
        if(imageDocumentOptional.isPresent()) {
            ImageDocument imageDocument = imageDocumentOptional.get();
            imageDocument.setTrash(true);
            imageRepositoryEs.save(imageDocument);
        }
        return 1;
    }

    @Transactional
    public void setFavorite(String imageId, Boolean favorite, UserDocument userDocument) {
        Optional<ImageDocument> imageDocumentOptional = imageRepositoryEs.findById(imageId);
        if(imageDocumentOptional.isPresent()) {
            ImageDocument imageDocument = imageDocumentOptional.get();
            imageDocument.setFavorite(favorite);
            imageRepositoryEs.save(imageDocument);
        }
    }

    @Transactional
    public void restoreImage(String imageId, UserDocument userDocument) {
        Optional<ImageDocument> imageDocumentOptional = imageRepositoryEs.findById(imageId);
        if(imageDocumentOptional.isPresent()) {
            ImageDocument imageDocument = imageDocumentOptional.get();
            imageDocument.setTrash(false);
            imageRepositoryEs.save(imageDocument);
        }
    }

    public ListResponse<ImageDocument> getFavoriteImages(Integer pageNo, Integer sort, Integer itemsPerPage, UserDocument userDocument) {

        List<ImageDocument> imageDocuments = imageRepositoryEs.findByUserIdAndFavoriteAndTrashOrderByCreatedAtDesc(userDocument.getId(), true, false);

        ListResponse<ImageDocument> listResponse = new ListResponse<>();
        listResponse.setPageNo(pageNo);
        listResponse.setItemsPerPage(itemsPerPage);
        listResponse.setListData(imageDocuments);
        listResponse.setTotalCount(imageDocuments.size());
        return listResponse;
    }

    public void replaceImage(ImageDTO imageDTO, UserDocument userDocument) {
        try {
            Optional<ImageDocument> optionalImageDocument = imageRepositoryEs.findById(imageDTO.getId());
            if(optionalImageDocument.isPresent()) {
                ImageDocument imageDocument = optionalImageDocument.get();
                File file = appUtils.toFile(imageDTO.getFile(), imageDTO.getImageName());
                Map resultMap = cloudinaryUtils.replaceImage(file, imageDocument.getPublicId());
                ImageDocument updatedImageDocument = ImageDocument.createDocument(resultMap, userDocument.getId());
                updatedImageDocument.setId(imageDocument.getId());
                updatedImageDocument.setUserId(imageDocument.getUserId());
                updatedImageDocument.setTrash(imageDocument.isTrash());
                updatedImageDocument.setFavorite(imageDocument.isFavorite());
                imageRepositoryEs.save(updatedImageDocument);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
