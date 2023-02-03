package com.jayesh.onlinephotogallery.entities.es;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Document(indexName = AlbumDocument.INDEX_NAME)
@Setter
@Getter
@NoArgsConstructor
public class AlbumDocument {

    @Transient
    public static final String INDEX_NAME = "photo_gallery_albums";

    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "album_name")
    private String albumName;

    @Field(type = FieldType.Keyword, name = "album_thumbnail")
    private String albumThumbnail;

    @Field(type = FieldType.Keyword, name = "images")
    private List<String> images;

    @Field(type = FieldType.Text, name = "user_id")
    private String userId;

}
