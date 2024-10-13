import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  onSubmit() {
    this.authService.login(this.username, this.password)
      .subscribe({
        next: (response) => {
          console.log('Login successful', response);
          // Handle successful login
          this.router.navigate(['/dashboard']).then(r => console.log(r));
        },
        error: (error) => {
          console.error('Login failed', error);
          this.errorMessage = 'Invalid username or password';
        }
      });
  }

  navigateToRegister() {
    this.router.navigate(['/register']).then(r => console.log(r));
  }
}
