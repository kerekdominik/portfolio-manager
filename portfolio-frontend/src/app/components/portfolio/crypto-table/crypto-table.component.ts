import {Component, OnInit} from '@angular/core';
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
import {CurrencyPipe, DatePipe} from '@angular/common';
import {CryptoPriceService} from '../../services/crypto-price.service';
import {CryptoDialogComponent} from './crypto-dialog/crypto-dialog.component';

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
    MatButton,
    DatePipe
  ],
  templateUrl: './crypto-table.component.html',
  styleUrl: './crypto-table.component.css'
})
export class CryptoTableComponent implements OnInit {
  displayedColumns: string[] = ['name', 'date', 'price', 'currentPrice', 'quantity', 'group', 'actions'];
  dataSource = [
    { name: 'Bitcoin', price: 50000, currentPrice: 51000, quantity: 2, group: 'Top Cryptos' },
    { name: 'Ethereum', price: 4000, currentPrice: 4200, quantity: 5, group: 'Altcoins' }
  ];

  cryptoNames: string[] = [];

  constructor(public dialog: MatDialog, private cryptoService: CryptoPriceService) {}

  ngOnInit(): void {
    this.cryptoService.getAllCryptoNames().subscribe(names => {
      this.cryptoNames = names;
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(CryptoDialogComponent, {
      width: '300px',
      data: {
        cryptoNames: this.cryptoNames
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataSource = [...this.dataSource, result];
      }
    });
  }

  openEditDialog(element: any): void {
    const dialogRef = this.dialog.open(CryptoDialogComponent, {
      width: '300px',
      data: { element, cryptoNames: this.cryptoNames }
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
}
