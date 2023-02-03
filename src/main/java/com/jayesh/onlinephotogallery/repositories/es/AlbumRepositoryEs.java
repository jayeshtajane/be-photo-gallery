package com.jayesh.onlinephotogallery.repositories.es;

import com.jayesh.onlinephotogallery.entities.es.AlbumDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface AlbumRepositoryEs extends ElasticsearchRepository<AlbumDocument, String> {

    Long removeByAlbumNameAndUserId(String imageId, String userId);

    List<AlbumDocument> removeByAlbumNameInAndUserId(List<String> folderNames, String userId);

    AlbumDocument findByAlbumNameAndUserId(String folderName, String userId);

    List<AlbumDocument> findByUserId(String id);

    List<AlbumDocument> findByImagesContainsAndUserId(String id, String userId);

}
