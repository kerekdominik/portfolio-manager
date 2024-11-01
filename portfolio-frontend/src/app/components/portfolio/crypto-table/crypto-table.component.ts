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
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {CurrencyPipe} from '@angular/common';
import {EditItemDialogComponent} from '../edit-item-dialog/edit-item-dialog.component';
import {CryptoPriceService} from '../../services/crypto-price.service';

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
    CurrencyPipe,
    MatButton
  ],
  templateUrl: './crypto-table.component.html',
  styleUrl: './crypto-table.component.css'
})
export class CryptoTableComponent {
  displayedColumns: string[] = ['name', 'price', 'currentPrice', 'quantity', 'group', 'actions'];
  dataSource = [
    { name: 'Bitcoin', price: 50000, currentPrice: 51000, quantity: 2, group: 'Top Cryptos' },
    { name: 'Ethereum', price: 4000, currentPrice: 4200, quantity: 5, group: 'Altcoins' }
  ]

  cryptoNames: string[] = [];

  constructor(public dialog: MatDialog, public cryptoService: CryptoPriceService) {}

  ngOnInit(): void {
    this.cryptoService.getAllCryptoNames().subscribe(names => {
      this.cryptoNames = names;
    });
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
        fields: ['name', 'price', 'quantity', 'group'],
        cryptoNames: this.cryptoNames
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataSource = [...this.dataSource, result];
      }
    });
  }
}
