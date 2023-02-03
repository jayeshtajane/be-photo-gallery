package com.jayesh.onlinephotogallery.controllers;

import com.jayesh.onlinephotogallery.entities.es.AlbumDocument;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.services.AlbumService;
import com.jayesh.onlinephotogallery.services.UserService;
import com.jayesh.onlinephotogallery.util.AppResponse;
import com.jayesh.onlinephotogallery.util.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @RequestMapping("/create-album/{albumName}")
    public AppResponse<AlbumDocument> createAlbum(@PathVariable String albumName, HttpServletRequest httpServletRequest) {
        AppResponse<AlbumDocument> appResponse = new AppResponse<>();
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        AlbumDocument albumDocument = null;
        try {
            albumDocument = albumService.createAlbum(albumName, userDocument);
            appResponse.setStatusCode(200);
            appResponse.setMessage("Album created successfully.");
        }
        catch(DataIntegrityViolationException e) {
            appResponse.setStatusCode(500);
            appResponse.setMessage("Album name already exist.");
        } catch (IOException e) {
            appResponse.setStatusCode(500);
            appResponse.setMessage("Error occurred while creating album.");
            e.printStackTrace();
        }
        appResponse.setEntity(albumDocument);
        return appResponse;
    }

    @RequestMapping(path = "/rename-album", method = RequestMethod.POST)
    public AppResponse<AlbumDocument> renameAlbum(@RequestBody AlbumDocument albumDocument, HttpServletRequest httpServletRequest) {
        AppResponse<AlbumDocument> appResponse = new AppResponse<>();
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        try {
            albumService.renameAlbum(albumDocument, userDocument);
            appResponse.setStatusCode(200);
            appResponse.setMessage("Album updated successfully.");
        }
        catch(DataIntegrityViolationException e) {
            appResponse.setStatusCode(500);
            appResponse.setMessage("Album name already exist.");
        }
        appResponse.setEntity(albumDocument);
        return appResponse;
    }

    @RequestMapping(path = "/delete-album/{albumName}", method = RequestMethod.DELETE)
    public Long deleteAlbum(@PathVariable String albumName, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return albumService.deleteAlbum(albumName, userDocument);
    }

    @RequestMapping(path = "/delete-album", method = RequestMethod.PUT)
    public List<AlbumDocument> deleteSelectedAlbum(@RequestBody List<String> albumNames, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return albumService.deleteSelectedAlbum(albumNames, userDocument);
    }

    @RequestMapping(path = "/add-images-to-album", method = RequestMethod.POST)
    public Long addImagesToAlbum(@RequestBody List<String> imageIds, @RequestParam String albumName, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return albumService.addImagesToAlbum(imageIds, albumName, userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getAll")
    public ListResponse<AlbumDocument> getAlbums(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return albumService.getAlbums(pageNo, sort, itemsPerPage, userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/get-images")
    public List<ImageDocument> getAlbumImages(@RequestParam String albumName, HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return albumService.getAlbumImages(albumName, userDocument);
    }
}
