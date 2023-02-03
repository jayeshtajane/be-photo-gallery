package com.jayesh.onlinephotogallery.entities.es;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import javax.persistence.Transient;

@Document(indexName = UserDocument.INDEX_NAME)
@Setter
@Getter
@NoArgsConstructor
public class UserDocument {

    @Transient
    public static final String INDEX_NAME = "photo_gallery_users";

    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "name")
    private String name;

    @Field(type = FieldType.Keyword, name = "email")
    private String email;

    @Field(type = FieldType.Text, name = "password")
    private String password;

    @Field(type = FieldType.Boolean, name = "email_verified")
    private boolean emailVerified;

}
