package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonAlias("ImdbID")
    private String imdbID;

    @Column(unique = true, nullable = false)
    @JsonAlias("Title")
    private String title;

    @JsonAlias("Year")
    private String year;
    @JsonAlias("Genre")
    private String genre;
    @JsonAlias("Director")
    private String director;
    @JsonAlias("Poster")
    private String poster;
    @JsonAlias("Plot")
    private String plot;


    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonAlias("Ratings")
    private List<Rating> ratings;

}
