import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/envrionment.dev';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) { }

  createGroup(name: string): Observable<string> {
    const group = { name };
    return this.http.post<string>(`${this.baseUrl}/groups`, group, { responseType: 'text' as 'json' });
  }
}
