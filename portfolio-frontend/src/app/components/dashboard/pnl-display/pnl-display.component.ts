import {Component, OnInit} from '@angular/core';
import {CurrencyPipe, NgClass, NgForOf, NgIf} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/envrionment.dev';
import {MatCard, MatCardContent} from '@angular/material/card';

@Component({
  selector: 'app-pnl-display',
  standalone: true,
  imports: [
    NgIf,
    NgClass,
    CurrencyPipe,
    MatCard,
    MatCardContent
  ],
  templateUrl: './pnl-display.component.html',
  styleUrls: ['./pnl-display.component.css']
})
export class PnlDisplayComponent implements OnInit {
  pnlSummary: { [key: string]: number } = {};

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadPnlSummary();
  }

  loadPnlSummary(): void {
    this.http.get<{ [key: string]: number }>(`${environment.apiBaseUrl}/dashboard/pnl-summary`).subscribe({
      next: (response) => {
        this.pnlSummary = response;
      },
      error: (err) => {
        console.error('Failed to load PNL summary', err);
      }
    });
  }

  get pnlEntries() {
    return Object.entries(this.pnlSummary).map(([key, value]) => ({ key, value }));
  }
}
