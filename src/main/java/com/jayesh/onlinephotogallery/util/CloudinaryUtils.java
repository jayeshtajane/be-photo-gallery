package com.jayesh.onlinephotogallery.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryUtils {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private AppUtils appUtils;

    public Map uploadImage(Object image) throws IOException {
        Map params = ObjectUtils.asMap(
                "public_id", "photo_gallery/" + appUtils.generateId(),
                "overwrite", true,
                "resource_type", "image",
                "detection", "coco_v1",
                "auto_tagging", "0.6",
                "categorization", "google_tagging"
//                google_tagging,imagga_tagging,aws_rek_tagging
//                "auto_tagging", "0.6"
        );
        return cloudinary.uploader().upload(image, params);
    }

    public Map replaceImage(Object image, String publicId) throws IOException {
        Map params = ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "image"
        );
        return cloudinary.uploader().upload(image, params);
    }

    public Map deleteImage(String publicId) throws IOException {
        Map params = ObjectUtils.asMap();
        return cloudinary.uploader().destroy(publicId, params);
    }
}
