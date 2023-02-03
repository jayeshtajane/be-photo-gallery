package com.jayesh.onlinephotogallery.services;

import com.jayesh.onlinephotogallery.entities.es.AlbumDocument;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.repositories.es.AlbumRepositoryEs;
import com.jayesh.onlinephotogallery.repositories.es.ImageRepositoryEs;
import com.jayesh.onlinephotogallery.util.ListResponse;
import com.jayesh.onlinephotogallery.util.exceptions.AlbumNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepositoryEs albumRepositoryEs;

    @Autowired
    private ImageRepositoryEs imageRepositoryEs;

    @Autowired
    @Lazy
    private ImageService imageService;

    @Value("${album.default.thumbnail}")
    private String defaultAlbumThumbnail;

    public AlbumDocument createAlbum(String albumName, UserDocument userDocument) throws IOException {
        if(albumRepositoryEs.findByAlbumNameAndUserId(albumName, userDocument.getId()) != null) {
            throw new RuntimeException("Album name already exist.");
        }
        AlbumDocument albumDocument = new AlbumDocument();
        albumDocument.setAlbumName(albumName);
        albumDocument.setAlbumThumbnail(defaultAlbumThumbnail);
        albumDocument.setUserId(String.valueOf(userDocument.getId()));
        return albumRepositoryEs.save(albumDocument);
    }

    @Transactional
    public Integer renameAlbum(AlbumDocument albumDocument, UserDocument userDocument) {
        if(albumRepositoryEs.findByAlbumNameAndUserId(albumDocument.getAlbumName(), userDocument.getId()) != null) {
            throw new RuntimeException("Album name already exist.");
        }
        Optional<AlbumDocument> optionalAlbumDocument = albumRepositoryEs.findById(albumDocument.getId());
        if(optionalAlbumDocument.isPresent()) {
            AlbumDocument doc = optionalAlbumDocument.get();
            doc.setAlbumName(albumDocument.getAlbumName());
            albumRepositoryEs.save(doc);
            return 1;
        }
        return 0;
    }

    @Transactional
    public Long deleteAlbum(String albumName, UserDocument userDocument) {
        AlbumDocument albumDocument = albumRepositoryEs.findByAlbumNameAndUserId(albumName, userDocument.getId());
        if(albumDocument.getImages() != null && !albumDocument.getImages().isEmpty()) {
            imageService.deleteMultipleImage(albumDocument.getImages(), userDocument);
        }
        return albumRepositoryEs.removeByAlbumNameAndUserId(albumName, userDocument.getId());
    }

    @Transactional
    public List<AlbumDocument> deleteSelectedAlbum(List<String> albumNames, UserDocument userDocument) {
        List<AlbumDocument> albumDocuments = albumRepositoryEs.removeByAlbumNameInAndUserId(albumNames, userDocument.getId());
        albumDocuments.forEach(album -> {
            if(album.getImages() != null && !album.getImages().isEmpty()) {
                imageService.deleteMultipleImage(album.getImages(), userDocument);
            }
        });
        return albumDocuments;
    }

    @Transactional
    public Long addImagesToAlbum(List<String> imageIds, String albumName, UserDocument userDocument) {
        AlbumDocument albumDocument = albumRepositoryEs.findByAlbumNameAndUserId(albumName, userDocument.getId());
        if(albumDocument == null) {
            throw new AlbumNotFoundException("Album not found.");
        }
        if(albumDocument.getImages() == null) {
            albumDocument.setImages(new ArrayList<>(imageIds.size()));
        }
        albumDocument.getImages().addAll(imageIds);
        Optional<ImageDocument> optionalImageDocument = imageRepositoryEs.findById(imageIds.get(0));
        optionalImageDocument.ifPresent(imageDocument -> albumDocument.setAlbumThumbnail(imageDocument.getUrl()));
        albumRepositoryEs.save(albumDocument);
        return (long) imageIds.size();
    }

    @Transactional(readOnly = true)
    public ListResponse<AlbumDocument> getAlbums(Integer pageNo, Integer sort, Integer itemsPerPage, UserDocument userDocument) {
        List<AlbumDocument> albumDocuments = albumRepositoryEs.findByUserId(userDocument.getId());
        ListResponse<AlbumDocument> listResponse = new ListResponse<>();
        listResponse.setPageNo(pageNo);
        listResponse.setItemsPerPage(itemsPerPage);
        listResponse.setListData(albumDocuments);
        listResponse.setTotalCount(albumDocuments.size());
        return listResponse;
    }

    @Transactional(readOnly = true)
    public List<ImageDocument> getAlbumImages(String albumName, UserDocument userDocument) {
        AlbumDocument albumDocument = albumRepositoryEs.findByAlbumNameAndUserId(albumName, userDocument.getId());
        if(albumDocument == null) {
            throw new AlbumNotFoundException("Album not found.");
        }
        if(albumDocument.getImages() == null || albumDocument.getImages().isEmpty()) {
            return Collections.emptyList();
        }
        List<ImageDocument> imageDocuments = new ArrayList<>();
        Iterable<ImageDocument> imageDocumentIterable = imageRepositoryEs.findAllById(albumDocument.getImages());
        imageDocumentIterable.forEach(imageDocuments::add);
        return imageDocuments;
    }

    public void removeImageFromAlbums(String imageId, UserDocument userDocument) {

        List<AlbumDocument> albumDocuments = albumRepositoryEs.findByImagesContainsAndUserId(imageId, userDocument.getId());
        albumDocuments.forEach(album -> {
            album.getImages().remove(imageId);
            albumRepositoryEs.save(album);
        });
    }

}
