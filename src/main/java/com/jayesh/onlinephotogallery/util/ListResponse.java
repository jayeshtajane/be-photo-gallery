package com.jayesh.onlinephotogallery.util;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse<T> {
    private int itemsPerPage;
    private int pageNo;
    private int totalCount;
    private List<T> listData;
}
