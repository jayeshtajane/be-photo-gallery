package com.jayesh.onlinephotogallery.services;

import com.jayesh.onlinephotogallery.entities.es.AlbumDocument;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import com.jayesh.onlinephotogallery.repositories.es.ExploreRepositoryEs;
import com.jayesh.onlinephotogallery.util.AppResponse;
import com.jayesh.onlinephotogallery.util.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExploreService {

    @Autowired
    private ExploreRepositoryEs exploreRepositoryEs;

    public ListResponse<AlbumDocument> getTopByField(int pageNo, int sort, int itemsPerPage, String fieldName, UserDocument userDocument) {
        List<AlbumDocument> categories = exploreRepositoryEs.getTopByField(pageNo, itemsPerPage, fieldName, userDocument);
        ListResponse<AlbumDocument> listResponse = new ListResponse<>();
        listResponse.setListData(categories);
        listResponse.setPageNo(pageNo);
        listResponse.setItemsPerPage(itemsPerPage);
        listResponse.setTotalCount(categories.size());
        return listResponse;
    }

    public ListResponse<ImageDocument> filterByField(int pageNo, int sort, int itemsPerPage, String searchText, String fieldName, UserDocument userDocument) {
        List<ImageDocument> categories = exploreRepositoryEs.filterByField(pageNo, itemsPerPage, searchText, fieldName, userDocument);
        ListResponse<ImageDocument> listResponse = new ListResponse<>();
        listResponse.setListData(categories);
        listResponse.setPageNo(pageNo);
        listResponse.setItemsPerPage(itemsPerPage);
        listResponse.setTotalCount(categories.size());
        return listResponse;
    }
}
