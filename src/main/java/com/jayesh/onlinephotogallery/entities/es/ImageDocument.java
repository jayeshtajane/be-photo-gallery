package com.jayesh.onlinephotogallery.entities.es;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Document(indexName = ImageDocument.INDEX_NAME)
@Setter
@Getter
@NoArgsConstructor
public class ImageDocument {

    @Transient
    public static final String INDEX_NAME = "photo_gallery_images";

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "signature")
    private String signature;

    @Field(type = FieldType.Keyword, name = "format")
    private String format;

    @Field(type = FieldType.Keyword, name = "resource_type")
    private String resourceType;

    @Field(type = FieldType.Text, name = "secure_url")
    private String secureUrl;

    @Field(type = FieldType.Date, name = "created_at")
    private Date createdAt;

    @Field(type = FieldType.Text, name = "asset_id")
    private String assetId;

    @Field(type = FieldType.Text, name = "version_id")
    private String versionId;

    @Field(type = FieldType.Keyword, name = "type")
    private String type;

    @Field(type = FieldType.Integer, name = "version")
    private int version;

    @Field(type = FieldType.Text, name = "url")
    private String url;

    @Field(type = FieldType.Text, name = "public_id")
    private String publicId;

    @Field(type = FieldType.Keyword, name = "tags")
    private List<String> tags;

    @Field(type = FieldType.Text, name = "folder")
    private String folder;

    @Field(type = FieldType.Text, name = "original_filename")
    private String originalFilename;

    @Field(type = FieldType.Text, name = "api_key")
    private String apiKey;

    @Field(type = FieldType.Integer, name = "bytes")
    private int bytes;

    @Field(type = FieldType.Boolean, name = "overwritten")
    private boolean overwritten;

    @Field(type = FieldType.Integer, name = "width")
    private int width;

    @Field(type = FieldType.Text, name = "etag")
    private String etag;

    @Field(type = FieldType.Boolean, name = "placeholder")
    private boolean placeholder;

    @Field(type = FieldType.Integer, name = "height")
    private int height;

    @Field(type = FieldType.Text, name = "user_id")
    private String userId;

    @Field(type = FieldType.Boolean, name = "favorite")
    private boolean favorite;

    @Field(type = FieldType.Boolean, name = "trash")
    private boolean trash;

    public static ImageDocument createDocument(Map resultMap, String userId) throws ParseException {
        ImageDocument imageDocument = new ImageDocument();
        imageDocument.setSignature((String) resultMap.get("signature"));
        imageDocument.setFormat((String) resultMap.get("format"));
        imageDocument.setResourceType((String) resultMap.get("resource_type"));
        imageDocument.setSecureUrl((String) resultMap.get("secure_url"));
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        imageDocument.setCreatedAt(isoFormat.parse((String) resultMap.get("created_at")));
        imageDocument.setAssetId((String) resultMap.get("asset_id"));
        imageDocument.setVersionId((String) resultMap.get("version_id"));
        imageDocument.setType((String) resultMap.get("type"));
        imageDocument.setVersion((Integer) resultMap.get("version"));
        imageDocument.setUrl((String) resultMap.get("url"));
        imageDocument.setPublicId((String) resultMap.get("public_id"));
        imageDocument.setTags((List<String>) resultMap.get("tags"));
        imageDocument.setFolder((String) resultMap.get("folder"));
        imageDocument.setOriginalFilename((String) resultMap.get("original_filename"));
        imageDocument.setApiKey((String) resultMap.get("api_key"));
        imageDocument.setBytes((Integer) resultMap.get("bytes"));
        if(resultMap.containsKey("overwritten"))
            imageDocument.setOverwritten((Boolean) resultMap.get("overwritten"));
        imageDocument.setWidth((Integer) resultMap.get("width"));
        imageDocument.setEtag((String) resultMap.get("etag"));
        imageDocument.setPlaceholder((Boolean) resultMap.get("placeholder"));
        imageDocument.setHeight((Integer) resultMap.get("height"));
        imageDocument.setUserId(userId);
        imageDocument.setFavorite(false);
        imageDocument.setTrash(false);
        return imageDocument;
    }

}
