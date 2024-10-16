import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/envrionment.dev';
import {Observable} from 'rxjs'; // Adjust the path

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string) {
    const loginData = { username, password };
    return this.http.post(`${this.baseUrl}/auth/login`, loginData);
  }

  register(username: string, password: string, email: string, firstName: string, lastName: string): Observable<any> {
    const registrationData = { username, password, email, firstName, lastName };
    return this.http.post(`${this.baseUrl}/auth/register`, registrationData);
  }
}
