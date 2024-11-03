import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/envrionment.dev';

@Injectable({
  providedIn: 'root'
})
export class CryptoPriceService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) { }

  getCurrentPrice(id: string): Observable<{ cryptoId: string, price: number }> {
    const params = new HttpParams().set('id', id);
    return this.http.get<{ cryptoId: string, price: number }>(`${this.baseUrl}/crypto/current/price`, { params });
  }

  getOneYearAgoPrice(id: string, date: string): Observable<{ cryptoId: string, date: string, price: number }> {
    const params = new HttpParams().set('id', id).set('date', date);
    return this.http.get<{ cryptoId: string, date: string, price: number }>(`${this.baseUrl}/crypto/historical/price`, { params });
  }

  getAllCryptoNames(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/crypto/list`);
  }
}
