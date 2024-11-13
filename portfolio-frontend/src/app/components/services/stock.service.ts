import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/envrionment.dev';

export interface Stock {
  id: string;
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

  getAllStockNames(): Observable<{ id: string; symbol: string; name: string; exchange: string }[]> {
    return this.http.get<{ id: string; symbol: string; name: string; exchange: string }[]>(`${this.baseUrl}/stock/list`);
  }

  getAllStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.baseUrl}/stock`);
  }

  createStock(stock: Stock): Observable<Stock> {
    return this.http.post<Stock>(`${this.baseUrl}/stock`, stock);
  }

  updateStock(id: string, stock: Stock): Observable<Stock> {
    return this.http.put<Stock>(`${this.baseUrl}/stock/${id}`, stock);
  }

  deleteStock(id: string,): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/stock/${id}`);
  }
}
