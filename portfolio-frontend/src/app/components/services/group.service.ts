import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/envrionment.dev';

export interface Group {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) { }

  getAllGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.baseUrl}/groups`);
  }

  createGroup(name: string): Observable<string> {
    const group = { name };
    return this.http.post<string>(`${this.baseUrl}/groups`, group, { responseType: 'text' as 'json' });
  }

  updateGroup(id: number, name: string): Observable<any> {
    const group = { name };
    return this.http.put<any>(`${this.baseUrl}/groups/${id}`, group);
  }

  deleteGroup(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/groups/${id}`);
  }
}
