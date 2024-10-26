import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/envrionment.dev';

@Injectable({
  providedIn: 'root'
})
export class CryptoPriceService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  getCurrentPrice(id: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/crypto/current/price?id=${id}`);
  }

  getOneYearAgoPrice(id: string, date: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/crypto/historical/price?id=${id}&date=${date}`);
  }
}
