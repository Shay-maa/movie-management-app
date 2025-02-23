import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MovieControllerService} from "../../services/services/movie-controller.service";
import {CommonModule} from "@angular/common";
import {Movie} from "../../services/models/movie";
import {NavbarComponent} from "../navbar/navbar.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, FormsModule, NavbarComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  searchForm: FormGroup;
  movies: any[] = [];
  loading: boolean = false;
  existingMovies: any[] = [];
  errorMessage: string = '';
  currentPage: number = 0;
  totalPages: number = 1;
  pageSize: number = 12;
  sCurrentPage: number = 0;
  searchTotalPages: number = 1;

  constructor(
    private fb: FormBuilder,
    private movieService: MovieControllerService
  ) {
    this.searchForm = this.fb.group({
      searchQuery: ['']
    });
  }

  ngOnInit(): void {
    this.getAllMovies();
  }

  onSearch(page: number = 0) {
    const query = this.searchForm.get('searchQuery')?.value;
    if (!query) {
      return;
    }
    this.loading = true;
    this.errorMessage = '';

    this.movieService.searchMoviesUsingPagination(
      {'title': query, 'page': page, 'size': this.pageSize}).subscribe({
      next: (response) => {
        this.loading = false;
        if (!response || response.content!.length === 0) {
          this.errorMessage = 'There is No movies'
          this.movies = [];
          this.searchTotalPages = 0;
        } else {
          this.movies = response.content!;
          this.searchTotalPages = response.totalPages!;
          this.sCurrentPage = response.number!;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error fetching movies.';
        this.movies = [];

      }
    });
  }

  getAllMovies(page: number = 0) {
    this.movieService.getMovies({'page': page, 'size': this.pageSize}).subscribe({
      next: (response) => {
        this.existingMovies = response.content?.map(movie => ({
          ...movie,
          selected: false
        })) || [];
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

  searchPreviousPage() {
    if (this.sCurrentPage > 0) {
      this.onSearch(this.sCurrentPage - 1);
    }
  }

  searchNextPage() {
    if (this.sCurrentPage < this.searchTotalPages - 1) {
      this.onSearch(this.sCurrentPage + 1);
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.getAllMovies(this.currentPage - 1);
    }
  }

  addSelected() {
    const selectedMovies = this.movies.filter(movie => movie.selected);

    const newMovies = selectedMovies.filter(movie => !this.isMovieAlreadyAdded(movie));
    const selectedMovieIds = newMovies.map(movie => movie.imdbID);

    if (selectedMovieIds.length === 0) {
      alert('No movies selected!');
      return;
    }

    this.movieService.batchAddMoviesFromOmdb({body: selectedMovieIds}).subscribe({
      next: () => {
        alert('Movies added successfully');
        this.getAllMovies();
        this.movies.forEach(movie => movie.selected = false);
      },
      error: () => alert('Failed to add movies')
    });
  }

  removeSelected() {
    const selectedIds = this.existingMovies.filter(movie => movie.selected).map(movie => movie.id);
    if (selectedIds.length === 0) return;

    this.movieService.deleteMovie({body: selectedIds}).subscribe({
      next: () => {
        alert('Movies removed successfully');
        this.getAllMovies();
      },
      error: () => {
        alert('Failed to remove movies.');
      }
    });
  }

  hasSelections(movieList: any[]): boolean {
    return movieList.some(movie => movie.selected);
  }

  selectedCount(movieList: any[]): number {
    return movieList.filter(movie => movie.selected).length;
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

  isMovieAlreadyAdded(movie: Movie): boolean {
    return this.existingMovies.some(existingMovie => existingMovie.imdbID === movie.imdbID);
  }
}
