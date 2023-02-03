package com.jayesh.onlinephotogallery.components.callbacks.es;

import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.util.AppUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.event.BeforeConvertCallback;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

@Component
public class UserBeforeConvertCallback implements BeforeConvertCallback<UserDocument> {

    @Autowired
    private AppUtils appUtils;

    @Override
    public UserDocument onBeforeConvert(UserDocument entity, IndexCoordinates index) {

        if(StringUtils.isEmpty(entity.getId())) {
            entity.setId(appUtils.generateId());
        }

        return entity;
    }

}
