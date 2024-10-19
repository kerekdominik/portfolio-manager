import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/envrionment.dev';
import {Observable, tap} from 'rxjs'; // Adjust the path

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string) {
    const loginData = { email, password };
    return this.http.post<{ token: string }>(`${this.baseUrl}/auth/login`, loginData)
      .pipe(
        tap(response => {
          localStorage.setItem('jwtToken', response.token);
        })
      );
  }

  register(username: string, password: string, email: string, firstName: string, lastName: string): Observable<any> {
    const registrationData = { username, password, email, firstName, lastName };
    return this.http.post(`${this.baseUrl}/auth/register`, registrationData);
  }

  googleLogin() {
    window.location.href = `${this.baseUrl}/auth/oauth2/authorize`;
  }

  githubLogin() {
    window.location.href = `${this.baseUrl}/auth/oauth2/authorize`;
  }

  logout() {
    localStorage.removeItem('jwtToken');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('jwtToken');
  }
}
