import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  email: string = '';
  firstName: string = '';
  lastName: string = '';
  errorMessage: string = '';

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  onSubmit() {
    // Access form data using component properties
    console.log('Registration form submitted', {
      username: this.username,
      password: this.password,
      email: this.email,
      firstName: this.firstName,
      lastName: this.lastName
    });

    this.authService.register(this.username, this.password, this.email, this.firstName, this.lastName)
      .subscribe({
        next: (response) => {
          console.log('Registration successful', response);
          // Handle successful registration, navigation to login page
          this.router.navigate(['']).then(r => console.log(r));
        },
        error: (error) => {
          console.error('Registration failed', error);
          this.errorMessage = 'Invalid registration data';
        }
      });
  }

  passwordsMatch(): boolean {
    return this.password === this.confirmPassword;
  }
}
