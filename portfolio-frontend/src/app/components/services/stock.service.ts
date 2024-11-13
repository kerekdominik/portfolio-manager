import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/envrionment.dev';

export interface Stock {
  symbol: string;
  name: string;
  exchange: string;
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
export class StockService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  getCurrentPrice(symbol: string): Observable<{ stockSymbol: string; price: number }> {
    const params = new HttpParams().set('symbol', symbol);
    return this.http.get<{ stockSymbol: string; price: number }>(`${this.baseUrl}/stock/current/price`, { params });
  }

  getOneYearAgoPrice(symbol: string, date: string): Observable<{ stockSymbol: string; date: string; price: number }> {
    const params = new HttpParams().set('symbol', symbol).set('date', date);
    return this.http.get<{ stockSymbol: string; date: string; price: number }>(`${this.baseUrl}/stock/historical/price`, { params });
  }

  getAllStockNames(): Observable<{ symbol: string; name: string; exchange: string }[]> {
    return this.http.get<{ symbol: string; name: string; exchange: string }[]>(`${this.baseUrl}/stock/list`);
  }

  getAllStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.baseUrl}/stock`);
  }

  getStockBySymbol(symbol: string): Observable<Stock> {
    return this.http.get<Stock>(`${this.baseUrl}/stock/${symbol}`);
  }

  createStock(stock: Stock): Observable<Stock> {
    return this.http.post<Stock>(`${this.baseUrl}/stock`, stock);
  }

  updateStock(symbol: string, stock: Stock): Observable<Stock> {
    return this.http.put<Stock>(`${this.baseUrl}/stock/${symbol}`, stock);
  }

  deleteStock(symbol: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/stock/${symbol}`);
  }
}
