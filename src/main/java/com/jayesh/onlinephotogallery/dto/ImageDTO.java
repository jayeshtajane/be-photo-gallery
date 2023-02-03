package com.jayesh.onlinephotogallery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ImageDTO {

    private String id;
    private String imageName;
    private String imageType;
    private int imageSize;
    private boolean favorite;
    private String url;

    private MultipartFile file;

}
