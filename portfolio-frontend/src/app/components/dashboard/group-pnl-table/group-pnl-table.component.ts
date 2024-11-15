import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/envrionment.dev';
import {HttpClient} from '@angular/common/http';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {CurrencyPipe, NgClass} from '@angular/common';

@Component({
  selector: 'app-group-pnl-table',
  standalone: true,
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCellDef,
    MatCell,
    NgClass,
    CurrencyPipe,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow
  ],
  templateUrl: './group-pnl-table.component.html',
  styleUrl: './group-pnl-table.component.css'
})
export class GroupPnlTableComponent implements OnInit {
  displayedColumns: string[] = ['groupName', 'pnl'];
  dataSource: { groupName: string; pnl: number }[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadGroupPnl();
  }

  loadGroupPnl(): void {
    this.http
      .get<{ [key: string]: number }>(`${environment.apiBaseUrl}/dashboard/group-pnl`)
      .subscribe({
        next: (response) => {
          this.dataSource = Object.entries(response).map(([groupName, pnl]) => ({
            groupName,
            pnl
          }));
        },
        error: (err) => {
          console.error('Failed to load group PNL', err);
        }
      });
  }

  getRowClass(pnl: number): string {
    return pnl >= 0 ? 'positive-pnl' : 'negative-pnl';
  }
}
