package com.jayesh.onlinephotogallery.controllers;

import com.jayesh.onlinephotogallery.entities.es.AlbumDocument;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.services.ExploreService;
import com.jayesh.onlinephotogallery.services.UserService;
import com.jayesh.onlinephotogallery.util.AppResponse;
import com.jayesh.onlinephotogallery.util.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/explore")
public class ExploreController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExploreService exploreService;

    @RequestMapping(method = RequestMethod.GET, path = "/things")
    public ListResponse<AlbumDocument> getThings(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return exploreService.getTopByField(pageNo, sort, itemsPerPage, "tags", userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/places")
    public ListResponse<AlbumDocument> getPlaces(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return exploreService.getTopByField(pageNo, sort, itemsPerPage, "places", userDocument);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/search")
    public ListResponse<ImageDocument> search(HttpServletRequest httpServletRequest, @RequestParam Integer pageNo, @RequestParam Integer sort, @RequestParam Integer itemsPerPage, @RequestParam String searchText) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        return exploreService.filterByField(pageNo, sort, itemsPerPage, searchText, "tags", userDocument);
    }

}
