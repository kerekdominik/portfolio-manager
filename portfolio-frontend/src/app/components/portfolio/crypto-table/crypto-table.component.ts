import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { CryptoService, Crypto } from '../../services/crypto.service';
import { CryptoDialogComponent } from './crypto-dialog/crypto-dialog.component';

@Component({
  selector: 'app-crypto-table',
  standalone: true,
  imports: [
    MatTable,
    MatButtonModule,
    MatIconModule,
    CurrencyPipe,
    DatePipe,
    MatHeaderCell,
    MatCell,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatRowDef,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef
  ],
  templateUrl: './crypto-table.component.html',
  styleUrls: ['./crypto-table.component.css']
})
export class CryptoTableComponent implements OnInit {
  displayedColumns: string[] = ['name', 'symbol', 'purchaseDate', 'price', 'currentPrice', 'quantity', 'groupName', 'actions'];
  dataSource: Crypto[] = [];
  cryptoList: { id: string; name: string, symbol: string }[] = [];

  constructor(
    public dialog: MatDialog,
    private cryptoService: CryptoService
  ) {}

  ngOnInit(): void {
    this.loadCryptos();
    this.loadCryptoList();
  }

  loadCryptos(): void {
    this.cryptoService.getAllCryptos().subscribe({
      next: (cryptos) => {
        this.dataSource = cryptos;
      },
      error: (error) => {
        console.error('Error fetching cryptos:', error);
      }
    });
  }

  loadCryptoList(): void {
    this.cryptoService.getAllCryptoNames().subscribe({
      next: (list) => {
        this.cryptoList = list;
      },
      error: (error) => {
        console.error('Error fetching crypto list:', error);
      }
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(CryptoDialogComponent, {
      width: '300px',
      data: {
        cryptoList: this.cryptoList
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadCryptos();
      }
    });
  }

  openEditDialog(element: Crypto): void {
    const dialogRef = this.dialog.open(CryptoDialogComponent, {
      width: '300px',
      data: { element, cryptoList: this.cryptoList }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadCryptos();
      }
    });
  }

  delete(element: Crypto): void {
    this.cryptoService.deleteCrypto(element.id).subscribe({
      next: () => {
        this.loadCryptos();
      },
      error: (error) => {
        console.error('Error deleting crypto:', error);
      }
    });
  }
}
