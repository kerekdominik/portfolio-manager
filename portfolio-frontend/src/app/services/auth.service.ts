import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/envrionment.dev';
import {Observable, tap} from 'rxjs';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;
  private tokenKey = 'authToken';

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  login(email: string, password: string) {
    const loginData = { email, password };
    return this.http.post<{ token: string }>(`${this.baseUrl}/auth/login`, loginData)
      .pipe(
        tap(response => {
          this.setToken(response.token);
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

  logout() {
    this.removeToken();
    this.router.navigate(['']).then(r => console.log(r));
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }
}
