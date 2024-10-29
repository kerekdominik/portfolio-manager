import { Component } from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {CurrencyPipe} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-stock-table',
  standalone: true,
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCellDef,
    MatHeaderCellDef,
    MatCell,
    CurrencyPipe,
    MatIconButton,
    MatIcon,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow
  ],
  templateUrl: './stock-table.component.html',
  styleUrl: './stock-table.component.css'
})
export class StockTableComponent {
  displayedColumns: string[] = ['name', 'price', 'quantity', 'actions'];
  dataSource = [
    { name: 'Apple', price: 150, quantity: 10 },
    { name: 'Tesla', price: 700, quantity: 3 }
  ];

  openEditDialog(element: any): void {
  }

  delete(element: any): void {
  }
}
