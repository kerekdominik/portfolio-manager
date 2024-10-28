import { Component } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
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
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-crypto-table',
  standalone: true,
  imports: [
    MatTable,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    MatColumnDef,
    MatIconButton,
    MatIcon,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatHeaderRowDef,
    CurrencyPipe
  ],
  templateUrl: './crypto-table.component.html',
  styleUrl: './crypto-table.component.css'
})
export class CryptoTableComponent {
  displayedColumns: string[] = ['name', 'price', 'quantity', 'actions'];
  dataSource = [
    { name: 'Bitcoin', price: 50000, quantity: 2 },
    { name: 'Ethereum', price: 4000, quantity: 5 }
  ];

  constructor(public dialog: MatDialog) {}

  openEditDialog(element: any): void {
    /*const dialogRef = this.dialog.open(EditCryptoDialogComponent, { data: element });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
      }
    });*/
  }

  delete(element: any): void {
  }
}
