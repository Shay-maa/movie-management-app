package com.example.demo.service;

import com.example.demo.dto.OmdbSearchResult;
import com.example.demo.entity.Movie;
import com.example.demo.entity.Rating;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final RestTemplate restTemplate;

    private final String API_KEY = "55259f65";
    private final String OMDB_API_URL = "http://www.omdbapi.com/";


    public Page<Movie> fetchMovieByTitlePagination(String title, Pageable pageable) {

        try {
            String url = OMDB_API_URL + "?s=" + title + "&page=" + (pageable.getPageNumber() + 1) + "&apikey=" + API_KEY;
            ResponseEntity<OmdbSearchResult> response = restTemplate.getForEntity(url, OmdbSearchResult.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OmdbSearchResult result = response.getBody();

                if ("True".equalsIgnoreCase(result.getResponse()) && result.getSearch() != null) {
                    List<Movie> movies = new ArrayList<>(result.getSearch());
                    int totalResults = Integer.parseInt(result.getTotalResults());

                    return new PageImpl<>(movies, pageable, totalResults);
                }
            } else {
                throw new RuntimeException("Failed to fetch data from OMDB API. Status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.err.println("Error fetching movies: " + e.getMessage());
        }

        return Page.empty();
    }


    public Movie saveMovie(String movieId) {
        String url = OMDB_API_URL + "?i=" + movieId + "&apikey=" + API_KEY;
        try {
            ResponseEntity<Movie> response = restTemplate.getForEntity(url, Movie.class);
            if (response.getBody() == null ||
                    response.getBody().getTitle() == null ||
                    response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Movie not found in OMDB API");
            }

            Movie movie = response.getBody();
            if (movie.getRatings() != null) {
                movie.getRatings().forEach(rating -> rating.setMovie(movie));
            }
            Optional<Movie> existingMovie = movieRepository.findByTitle(movie.getTitle());
            return existingMovie.orElseGet(() -> movieRepository.save(movie));
        }catch (RestClientException e) {
            throw new RestClientException("Error connecting to OMDB API for ID: " + movieId, e);
        }

    }


    public List<Movie> addMovies(List<String> movieIds) {
        List<Movie> savedMovies = new ArrayList<>();
        for (String movieId : movieIds) {
            try {
                Movie movie = saveMovie(movieId);
                savedMovies.add(movie);
            } catch (Exception e) {
                System.err.println("Error fetching movie with ID " + movieId + ": " + e.getMessage());
            }
        }
        return savedMovies;
    }


    public void deleteMovies(List<Long> movieIds) {
        List<Movie> moviesToDelete = movieRepository.findAllById(movieIds);
        movieRepository.deleteAll(moviesToDelete);
    }


    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }


    public Page<Movie>getMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }


    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found for id: " + id));
    }


    public List<Movie> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }


    public Movie addRating(Long movieId, String userEmail, int ratingValue) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        boolean alreadyRated = movie.getRatings().stream()
                .anyMatch(r->r.getSource().equals(userEmail));
        if(alreadyRated) {
            throw new IllegalStateException("You have already rated this movie.");
        }
        Rating rating = new Rating();
        rating.setMovie(movie);
        rating.setSource(userEmail);
        rating.setValue(String.valueOf(ratingValue));

        ratingRepository.save(rating);
        movie.getRatings().add(rating);
        return movieRepository.save(movie);
    }

    public List<Movie> fetchMovieByTitle(String title) {
        List<Movie> allMovies = new ArrayList<>();
        int page = 1;
        int maxResults = 21;
        try{
            while (allMovies.size() < maxResults) {
                String url = OMDB_API_URL + "?s=" + title + "&page="+page + "&apikey=" + API_KEY;
                ResponseEntity<OmdbSearchResult> response = restTemplate.getForEntity(url, OmdbSearchResult.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    OmdbSearchResult result = response.getBody();
                    if ("True".equalsIgnoreCase(result.getResponse()) && result.getSearch() != null) {
                        allMovies.addAll(result.getSearch());
                        page++;
                    } else {
                        break;
                    }
                } else {
                    throw new RuntimeException("Failed to fetch data from OMDB API. Status: " + response.getStatusCode());
                }
            }
        } catch (RestClientException e) {
            System.err.println("Error fetching movies: " + e.getMessage());
        }
        return allMovies.size() > maxResults ? allMovies.subList(0, maxResults) : allMovies;
    }

}



