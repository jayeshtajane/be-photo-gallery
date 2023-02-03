package com.jayesh.onlinephotogallery.repositories.es;

import com.jayesh.onlinephotogallery.entities.es.AlbumDocument;
import com.jayesh.onlinephotogallery.entities.es.ImageDocument;
import com.jayesh.onlinephotogallery.entities.es.UserDocument;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExploreRepositoryEs {

    @Autowired
    private RestHighLevelClient client;

    public List<AlbumDocument> getTopByField(int pageNo, int itemPerPage, String fieldName, UserDocument userDocument) {
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("top" + fieldName)
                .field(fieldName)
                .minDocCount(1)
                .size(itemPerPage)
                .subAggregation(
                        AggregationBuilders.topHits("top_hits_" + fieldName)
                                .size(1)
                );
        SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(aggregation);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(new MatchQueryBuilder("user_id", userDocument.getId()));
        builder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest()
                .indices(ImageDocument.INDEX_NAME)
                .source(builder);
        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Aggregation> results = response.getAggregations().asMap();
        ParsedTerms topTags = (ParsedStringTerms) results.get("top" + fieldName);
        List<String> topHits = topTags.getBuckets()
                .stream()
                .map(bucket -> StringUtils.capitalize(bucket.getKeyAsString()))
                .collect(Collectors.toList());

        return topHits.stream().map(s -> {
            AlbumDocument albumDocument = new AlbumDocument();
            albumDocument.setAlbumName(s);
            String url = (String) ((ParsedTopHits) topTags.getBuckets().get(0).getAggregations().get("top_hits_" + fieldName))
                    .getHits().getHits()[0].getSourceAsMap().get("url");
            albumDocument.setAlbumThumbnail(url);
            return albumDocument;
        }).collect(Collectors.toList());
    }

    public List<ImageDocument> filterByField(int pageNo, int itemPerPage, String searchText, String fieldName, UserDocument userDocument) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                .must(new MatchQueryBuilder("user_id", userDocument.getId()))
                .must(new MatchQueryBuilder(fieldName, searchText));;
        builder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest()
                .indices(ImageDocument.INDEX_NAME)
                .source(builder);
        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.stream(response.getHits().getHits())
                .map(documentFields -> {
                    Map<String, Object> resultMap = documentFields.getSourceAsMap();
                    try {
                        return ImageDocument.createDocument(resultMap, userDocument.getId());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
