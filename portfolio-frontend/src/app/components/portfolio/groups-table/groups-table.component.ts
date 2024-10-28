import { Component } from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-groups-table',
  standalone: true,
  imports: [
    MatTable,
    MatHeaderCell,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatCell,
    MatIconButton,
    MatIcon,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef
  ],
  templateUrl: './groups-table.component.html',
  styleUrl: './groups-table.component.css'
})
export class GroupsTableComponent {
  displayedColumns: string[] = ['name', 'type', 'actions'];
  dataSource = [
    { name: 'Tech Stocks', type: 'Stock' },
    { name: 'Top Cryptos', type: 'Crypto' }
  ];

  openEditDialog(element: any): void {
  }

  delete(element: any): void {
  }
}
