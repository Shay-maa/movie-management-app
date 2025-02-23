import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {Movie} from "../../services/models/movie";
import {MovieControllerService} from "../../services/services/movie-controller.service";
import {NavbarComponent} from "../navbar/navbar.component";
import {RateMovie$Params} from "../../services/fn/movie-controller/rate-movie";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule, NavbarComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  searchForm: FormGroup;
  movies: Movie[] = [];
  loading: boolean = false;
  existingMovies: Movie[] = [];
  errorMessage: string = '';
  selectedMovie: Movie | null = null;
  currentPage: number = 0;
  totalPages: number = 1;
  pageSize: number = 12;

  constructor(
    private fb: FormBuilder,
    private movieService: MovieControllerService,
  ) {
    this.searchForm = this.fb.group({
      searchQuery: ['']
    });
  }

  ngOnInit(): void {
    this.getAllMovies();
  }

  onSearch() {
    const query = this.searchForm.get('searchQuery')?.value;
    if (!query) {
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.movieService.searchMovies({title: query}).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.length === 0) {
          this.errorMessage = 'There is No movies'
        } else {
          this.movies = response;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error fetching movies.';
      }
    });
  }

  getAllMovies(page: number = 0) {
    this.movieService.getMovies({'page': page, 'size': this.pageSize}).subscribe({
      next: (response) => {
        this.existingMovies = response.content!;
        this.totalPages = response.totalPages!;
        this.currentPage = page;
      },
      error: () => {
        console.error("Error fetching movies.");
      }
    });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.getAllMovies(this.currentPage + 1);
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.getAllMovies(this.currentPage - 1);
    }
  }

  getNormalizedRating(rating: string): number {
    if (rating.includes('/100')) {
      return (parseFloat(rating.split('/')[0]) / 100) * 10;
    } else if (rating.includes('%')) {
      return (parseFloat(rating.replace('%', '')) / 100) * 10;
    } else if (rating.includes('/10')) {
      return parseFloat(rating.split('/')[0]);
    } else {
      return parseFloat(rating);
    }
  }

  getAverageRating(movie: Movie): number {
    if (!movie.ratings || movie.ratings.length === 0) return 0;

    const normalizedRatings = movie.ratings.map(r => this.getNormalizedRating(r.value!));
    const total = normalizedRatings.reduce((sum, value) => sum + value, 0);
    const average = total / normalizedRatings.length;

    return Math.min(average, 10);
  }

  viewMovieDetails(movie: Movie): void {
    this.loading = true;
    this.movieService.getMovieById({id: movie.id!}).subscribe({
      next: (details) => {
        this.selectedMovie = details;
        this.loading = false;

        const userEmail = this.getUserEmailFromToken();
        if (userEmail) {
          const userRating = details.ratings?.find(r => r.source === userEmail);
          this.userRatings[movie.id!] = userRating ? parseInt(userRating.value!, 10) : 0;
          this.tempRating = this.userRatings[movie.id!];
        } else {
          this.tempRating = 0;
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load movie details.';
        this.loading = false;
      }
    });
  }

  closeMovieDetails(): void {
    this.selectedMovie = null;
    this.errorMessage = '';
  }

  tempRating: number = 0;
  userRatings: { [movieId: number]: number } = {};
  rate: any = {id: 0, userEmail: '', rate: 0};

  rateMovie(rating: number, movieId: number) {

    const userEmail = this.getUserEmailFromToken();
    if (!userEmail) {
      this.errorMessage = 'Please log in to rate a movie.';
      return;
    }
    const rateParams: RateMovie$Params = {
      id: movieId,
      userEmail: userEmail,
      rate: rating
    };

    this.movieService.rateMovie(rateParams).subscribe({
      next: (updatedMovie) => {
        this.selectedMovie = updatedMovie;
        const userRating = updatedMovie.ratings!.find(r => r.source === userEmail);
        this.userRatings[movieId] = userRating ? parseInt(userRating.value!, 10) : 0;
        this.tempRating = this.userRatings[movieId];
        this.errorMessage = '';
      },
      error: (err) => {
        this.errorMessage = 'You have already rated this movie.';
      }
    });
  }


  hoverRating(rating: number) {
    this.tempRating = rating;
  }

  resetRating() {
    this.tempRating = this.userRatings[this.selectedMovie?.id!] || 0;
  }

  getUserEmailFromToken(): string | null {
    const token = localStorage.getItem('token');
    console.log(token)
    if (!token) return null;
    try {
      console.log(token)
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log(payload.sub)

      return payload.sub;
    } catch (e) {
      return null;
    }
  }

}
