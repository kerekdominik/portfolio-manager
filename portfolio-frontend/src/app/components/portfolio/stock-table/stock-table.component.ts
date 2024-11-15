import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
  MatTableModule,
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
} from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {CurrencyPipe, DatePipe, NgClass} from '@angular/common';
import { StockService, Stock } from '../../services/stock.service';
import { StockDialogComponent } from './stock-dialog/stock-dialog.component';

@Component({
  selector: 'app-stock-table',
  standalone: true,
  imports: [
    MatTableModule,
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
    MatHeaderRowDef,
    NgClass
  ],
  templateUrl: './stock-table.component.html',
  styleUrls: ['./stock-table.component.css']
})
export class StockTableComponent implements OnInit {
  displayedColumns: string[] = ['name', 'symbol', 'purchaseDate', 'price', 'quantity', 'originalValue', 'currentPrice', 'currentValue', 'pnl', 'groupName', 'actions'];
  dataSource: Stock[] = [];
  stockList: { symbol: string; name: string; exchange: string }[] = [];

  constructor(
    public dialog: MatDialog,
    private stockService: StockService
  ) {}

  ngOnInit(): void {
    this.loadStocks();
    this.loadStockList();
  }

  loadStocks(): void {
    this.stockService.getAllStocks().subscribe({
      next: (stocks) => {
        this.dataSource = stocks;
      },
      error: (error) => {
        console.error('Error fetching stocks:', error);
      }
    });
  }

  loadStockList(): void {
    this.stockService.getAllStockNames().subscribe({
      next: (list) => {
        this.stockList = list;
      },
      error: (error) => {
        console.error('Error fetching stock list:', error);
      }
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(StockDialogComponent, {
      width: '300px',
      data: {
        stockList: this.stockList
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStocks();
      }
    });
  }

  openEditDialog(element: Stock): void {
    const dialogRef = this.dialog.open(StockDialogComponent, {
      width: '300px',
      data: { element, stockList: this.stockList }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStocks();
      }
    });
  }

  delete(element: Stock): void {
    this.stockService.deleteStock(element.id).subscribe({
      next: () => {
        this.loadStocks();
      },
      error: (error) => {
        console.error('Error deleting stock:', error);
      }
    });
  }
}
