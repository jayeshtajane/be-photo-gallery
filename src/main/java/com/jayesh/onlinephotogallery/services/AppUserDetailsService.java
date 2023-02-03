package com.jayesh.onlinephotogallery.services;

import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.repositories.es.UserRepositoryEs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepositoryEs userRepositoryEs;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDocument userDocument = userRepositoryEs.findByEmail(s);
        return new User(userDocument.getEmail(), userDocument.getPassword(), new ArrayList<>());
    }
}
