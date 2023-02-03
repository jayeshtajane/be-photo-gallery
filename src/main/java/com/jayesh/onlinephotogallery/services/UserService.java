package com.jayesh.onlinephotogallery.services;

import com.jayesh.onlinephotogallery.components.JwtUtils;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.repositories.es.UserRepositoryEs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepositoryEs userRepositoryEs;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public UserDocument getUser(String userName) {
        return userRepositoryEs.findByEmail(userName);
    }

    public UserDocument saveUser(UserDocument user) {
        if(userRepositoryEs.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exist.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepositoryEs.save(user);
    }

    @Transactional
    public Integer updateUser(UserDocument user) {
        Optional<UserDocument> optionalUserDocument = userRepositoryEs.findById(user.getId());
        if(optionalUserDocument.isPresent()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            UserDocument userDocument = optionalUserDocument.get();
            userDocument.setName(user.getName());
            userDocument.setPassword(user.getPassword());
            userRepositoryEs.save(userDocument);
            return 1;
        }
        return 0;
    }

    public UserDocument getUserFromToken(HttpServletRequest httpServletRequest) {
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtils.getUsernameFromToken(jwt);
        }

        return userRepositoryEs.findByEmail(username);
    }

}
