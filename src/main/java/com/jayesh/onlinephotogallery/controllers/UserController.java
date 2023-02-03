package com.jayesh.onlinephotogallery.controllers;

import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.services.UserService;
import com.jayesh.onlinephotogallery.util.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/get-user", method = RequestMethod.GET)
    public AppResponse getUser(HttpServletRequest httpServletRequest) {
        UserDocument userDocument = userService.getUserFromToken(httpServletRequest);
        AppResponse<UserDocument> appResponse = new AppResponse<>();
        appResponse.setStatusCode(200);
        appResponse.setMessage("User fetched successfully");
        appResponse.setEntity(userDocument);
        return appResponse;
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public AppResponse saveUser(@RequestBody UserDocument user) {
        AppResponse<UserDocument> appResponse = new AppResponse<>();
        user.setId(null);
        try {
            user = userService.saveUser(user);
            appResponse.setStatusCode(200);
            appResponse.setMessage("User register successfully.");
        }
        catch(DataIntegrityViolationException e) {
            appResponse.setStatusCode(500);
            appResponse.setMessage("Email id already exist.");
        }
        appResponse.setEntity(user);
        return appResponse;
    }

    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    public AppResponse<UserDocument> updateUser(@RequestBody UserDocument user, HttpServletRequest httpServletRequest) {
        UserDocument loggedUser = userService.getUserFromToken(httpServletRequest);
        if(!loggedUser.getId().equals(user.getId())) {
            throw new AccessDeniedException("Operation not allowed.");
        }

        AppResponse<UserDocument> appResponse = new AppResponse<>();
        appResponse.setEntity(user);
        try {
            userService.updateUser(user);
            appResponse.setStatusCode(200);
            appResponse.setMessage("User updated successfully.");
        }
        catch(Exception e) {
            appResponse.setStatusCode(500);
            appResponse.setMessage("Error occurred while updating user.");
        }
        return appResponse;
    }

}
