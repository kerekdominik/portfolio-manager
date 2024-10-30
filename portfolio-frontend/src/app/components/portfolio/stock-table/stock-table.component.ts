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
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {EditItemDialogComponent} from '../edit-item-dialog/edit-item-dialog.component';
import {MatDialog} from '@angular/material/dialog';

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
    MatRow,
    MatButton
  ],
  templateUrl: './stock-table.component.html',
  styleUrl: './stock-table.component.css'
})
export class StockTableComponent {
  displayedColumns: string[] = ['name', 'price', 'currentPrice', 'quantity', 'group', 'actions'];
  dataSource = [
    { name: 'Apple', currentPrice: 155, price: 150, quantity: 10, group: 'Tech Stocks' },
    { name: 'Tesla', currentPrice: 705, price: 700, quantity: 3, group: 'Tech Stocks' }
  ];

  constructor(public dialog: MatDialog) {
  }

  openEditDialog(element: any): void {
    const dialogRef = this.dialog.open(EditItemDialogComponent, {
      width: '300px',
      data: {
        ...element,
        fields: ['name', 'price', 'quantity', 'group']
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const index = this.dataSource.findIndex(item => item.name === element.name);
        if (index > -1) {
          this.dataSource[index] = result;
          this.dataSource = [...this.dataSource];
        }
      }
    });
  }

  delete(element: any): void {
    this.dataSource = this.dataSource.filter(item => item.name !== element.name);
  }

  openAddDialog() {
    const dialogRef = this.dialog.open(EditItemDialogComponent, {
      width: '300px',
      data: {
        name: '',
        price: 0,
        currentPrice: 0,
        quantity: 0,
        group: '',
        fields: ['name', 'price', 'quantity', 'group']
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataSource = [...this.dataSource, result];
      }
    });
  }
}
