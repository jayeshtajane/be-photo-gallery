package com.jayesh.onlinephotogallery.util;

import com.google.common.io.Files;
import com.jayesh.onlinephotogallery.dto.ImageMetaDataHolder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class AppUtils {

    @Autowired
    private RestTemplate restTemplate;

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public File toFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(Files.createTempDir(), fileName);
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    public ImageMetaDataHolder readImageMetaData(File file) throws IOException, ImageReadException {
        final ImageMetadata metadata = Imaging.getMetadata(file);
        ImageMetaDataHolder imageMetaDataHolder = new ImageMetaDataHolder();
        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            // simple interface to GPS data
            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
            if (exifMetadata != null) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();
                    String locationData = getLocationDate(latitude + "", longitude + "");
                    imageMetaDataHolder.setLocation(locationData);
                }
            }
        }
        return imageMetaDataHolder;
    }

    private String getLocationDate(String latitude,String longitude) throws IOException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://api.positionstack.com/v1/reverse?access_key=31b9d8209e39b3f8edc37f096a7bc6dd&query=" + latitude + "," + longitude + "", String.class);
        System.out.println(responseEntity.getBody());
        return responseEntity.getBody();
    }

}
