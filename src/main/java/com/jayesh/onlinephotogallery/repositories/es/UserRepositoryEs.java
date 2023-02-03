package com.jayesh.onlinephotogallery.repositories.es;

import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepositoryEs extends ElasticsearchRepository<UserDocument, String> {

    UserDocument findByEmail(String email);

}
