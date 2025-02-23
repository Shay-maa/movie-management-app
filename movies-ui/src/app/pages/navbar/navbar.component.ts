import {Component} from '@angular/core';
import {Router, RouterLink} from "@angular/router";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  websiteName = 'Movies Hub 🍿';

  constructor(private router: Router) {
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('admin');
    this.router.navigate(['/login']);
  }
}
