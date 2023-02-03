package com.jayesh.onlinephotogallery.repositories.es;

import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ImageRepositoryEs extends ElasticsearchRepository<ImageDocument, String> {

    List<ImageDocument> findByUserIdAndTrashOrderByCreatedAtDesc(String id, boolean trash);

    ImageDocument findByIdAndUserId(String imageId, String userId);

    Long removeByIdAndUserId(String imageId, String userId);

    List<ImageDocument> removeByIdInAndUserId(List<String> ids, String userId);

    List<ImageDocument> findByUserIdAndFavoriteAndTrashOrderByCreatedAtDesc(String id, boolean favorite, boolean trash);

}
