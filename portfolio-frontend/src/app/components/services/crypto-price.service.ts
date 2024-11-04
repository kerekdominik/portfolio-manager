import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/envrionment.dev';

export interface Crypto {
  id: string;
  name: string;
  symbol: string;
  quantity: number;
  currentPrice: number;
  price: number;
  purchaseDate: string;
  groupId?: number;
  groupName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CryptoPriceService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  getCurrentPrice(id: string): Observable<{ cryptoId: string; price: number }> {
    const params = new HttpParams().set('id', id);
    return this.http.get<{ cryptoId: string; price: number }>(`${this.baseUrl}/crypto/current/price`, { params });
  }

  getOneYearAgoPrice(id: string, date: string): Observable<{ cryptoId: string; date: string; price: number }> {
    const params = new HttpParams().set('id', id).set('date', date);
    return this.http.get<{ cryptoId: string; date: string; price: number }>(`${this.baseUrl}/crypto/historical/price`, { params });
  }

  getAllCryptoNames(): Observable<{ id: string; name: string; symbol: string }[]> {
    return this.http.get<{ id: string; name: string; symbol: string }[]>(`${this.baseUrl}/crypto/list`);
  }

  getAllCryptos(): Observable<Crypto[]> {
    return this.http.get<Crypto[]>(`${this.baseUrl}/crypto`);
  }

  getCryptoById(id: string): Observable<Crypto> {
    return this.http.get<Crypto>(`${this.baseUrl}/crypto/${id}`);
  }

  createCrypto(crypto: Crypto): Observable<Crypto> {
    return this.http.post<Crypto>(`${this.baseUrl}/crypto`, crypto);
  }

  updateCrypto(id: string, crypto: Crypto): Observable<Crypto> {
    return this.http.put<Crypto>(`${this.baseUrl}/crypto/${id}`, crypto);
  }

  deleteCrypto(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/crypto/${id}`);
  }
}
