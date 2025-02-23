package com.example.demo.controller;

import com.example.demo.entity.Movie;
import com.example.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/search")
    public ResponseEntity<List<Movie>> searchMovie(@RequestParam String title) {
        List<Movie> movies = movieService.fetchMovieByTitle(title);
        return ResponseEntity.ok(movies);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/movies/search")
    public ResponseEntity<Page<Movie>> searchMoviesUsingPagination(
            @RequestParam String title,
            @ParameterObject Pageable pageable) {

        Page<Movie> movies = movieService.fetchMovieByTitlePagination(title,pageable);
        return ResponseEntity.ok(movies);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add")
    public ResponseEntity<Movie> addMovie(@RequestParam String movieId) {
        Movie savedMovie = movieService.saveMovie(movieId);
        return ResponseEntity.ok(savedMovie);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/batch-add")
    public ResponseEntity<List<Movie>> batchAddMoviesFromOmdb(@RequestBody List<String> movieIds) {
        List<Movie> addedMovies = movieService.addMovies(movieIds);
        return ResponseEntity.ok(addedMovies);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin")
    public ResponseEntity<Movie> deleteMovie(@RequestBody List<Long> moviesIds) {
        movieService.deleteMovies(moviesIds);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }


    @GetMapping("/get-movies")
    public Page<Movie> getMovies(@ParameterObject Pageable pageable) {
        return movieService.getMovies(pageable);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));

    }


    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMovies(@RequestParam("title") String title) {
        List<Movie> movies = movieService.searchMovies(title);
        return ResponseEntity.ok(movies);
    }


    @PostMapping("/{id}/rate")
    public ResponseEntity<Movie> rateMovie(@PathVariable Long id,
                                            @RequestParam String userEmail,
                                            @RequestParam int rate) {
        Movie updatedMovie = movieService.addRating(id, userEmail, rate);
        return ResponseEntity.ok(updatedMovie);
    }
}
