package com.example.demo.dto;

import com.example.demo.entity.Movie;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OmdbSearchResult {
    @JsonProperty("Search")
    private List<Movie> search;
    @JsonProperty("Response")
    private String response;
    @JsonProperty("totalResults")
    private String totalResults;
}