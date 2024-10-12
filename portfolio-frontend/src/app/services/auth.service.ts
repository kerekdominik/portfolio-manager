import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/envrionment.dev'; // Adjust the path

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

}
