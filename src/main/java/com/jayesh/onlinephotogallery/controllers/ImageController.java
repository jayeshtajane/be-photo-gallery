package com.jayesh.onlinephotogallery.controllers;

import com.jayesh.onlinephotogallery.dto.ImageDTO;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.services.ImageService;
import com.jayesh.onlinephotogallery.services.UserService;
import com.jayesh.onlinephotogallery.util.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, path = "/getAll")
    public ListResponse<ImageDocument> getImages(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage, @RequestParam String albumName) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.getImages(pageNo, sort, itemsPerPage, albumName, userDocument, false);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    public List<ImageDocument> uploadImages(HttpServletRequest httpServletRequest, @RequestParam("images") MultipartFile[] files) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        List<ImageDocument> imageEntities = Arrays.asList(files)
                .stream()
                .map(file -> imageService.storeFile(file, userDocument))
                .collect(Collectors.toList());

        imageService.saveImages(imageEntities);

        return imageEntities;
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{imageId}")
    public Long deleteImage(@PathVariable String imageId, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.deleteImage(imageId, userDocument);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/delete")
    public List<ImageDocument> deleteMultipleImage(@RequestBody List<String> imageIds, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.deleteMultipleImage(imageIds, userDocument);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/move-trash/{imageId}")
    public Integer moveImageToTrash(@PathVariable String imageId, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.moveImageToTrash(imageId, userDocument);
    }

    @RequestMapping(value = "/move-trash", method = RequestMethod.POST)
    public Integer moveMultipleImagesToTrash(@RequestBody List<String> ids, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.moveMultipleImagesToTrash(ids, userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/from-trash")
    public ListResponse<ImageDocument> getImagesFromTrash(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.getImages(pageNo, sort, itemsPerPage, null, userDocument, true);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/set-favorite/{imageId}")
    public void setFavorite(HttpServletRequest httpServletRequest, @PathVariable String imageId, @RequestParam Boolean favorite) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        imageService.setFavorite(imageId, favorite, userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restore-image/{imageId}")
    public void restoreImage(HttpServletRequest httpServletRequest, @PathVariable String imageId) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        imageService.restoreImage(imageId, userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/get-favorite-images")
    public ListResponse<ImageDocument> getFavoriteImages(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return imageService.getFavoriteImages(pageNo, sort, itemsPerPage, userDocument);
    }

    @RequestMapping(value="/edit-image",method = RequestMethod.POST)
    public void replaceImage(@ModelAttribute ImageDTO imageDTO, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        imageService.replaceImage(imageDTO, userDocument);
    }

}
