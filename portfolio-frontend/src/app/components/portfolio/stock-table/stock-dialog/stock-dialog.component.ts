import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { NgForOf, NgIf, AsyncPipe } from '@angular/common';
import { map, Observable, startWith } from 'rxjs';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Group, GroupService } from '../../../services/group.service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_NATIVE_DATE_FORMATS,
  MatNativeDateModule,
  NativeDateAdapter
} from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { StockService, Stock } from '../../../services/stock.service';

@Component({
  selector: 'app-stock-dialog',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormFieldModule,
    MatInputModule,
    MatDialogActions,
    MatButtonModule,
    MatSelectModule,
    NgForOf,
    ReactiveFormsModule,
    MatAutocompleteModule,
    AsyncPipe,
    NgIf,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  providers: [
    { provide: DateAdapter, useClass: NativeDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS },
  ],
  templateUrl: './stock-dialog.component.html',
  styleUrls: ['./stock-dialog.component.css']
})
export class StockDialogComponent implements OnInit {
  groups: Group[] = [];
  stockForm: FormGroup;
  filteredStockList: Observable<{ symbol: string; name: string; exchange: string }[]>;
  isEditMode: boolean;
  stockSymbol?: string;

  constructor(
    public dialogRef: MatDialogRef<StockDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { element?: Stock; stockList: { symbol: string; name: string; exchange: string }[] },
    private groupService: GroupService,
    private stockService: StockService
  ) {
    this.isEditMode = !!data.element;

    this.stockForm = new FormGroup({
      name: new FormControl('', Validators.required),
      symbol: new FormControl({ value: '', disabled: true }),
      exchange: new FormControl({ value: '', disabled: true }),
      quantity: new FormControl('', Validators.required),
      price: new FormControl('', Validators.required),
      purchaseDate: new FormControl(new Date(), Validators.required),
      groupId: new FormControl(''),
    });

    if (this.isEditMode && data.element) {
      this.stockSymbol = data.element.symbol;
      this.stockForm.patchValue({
        name: data.element.name,
        symbol: data.element.symbol,
        exchange: data.element.exchange,
        quantity: data.element.quantity,
        price: data.element.price,
        purchaseDate: new Date(data.element.purchaseDate),
        groupId: data.element.groupId,
      });
    }

    this.filteredStockList = new Observable<{ symbol: string; name: string; exchange: string }[]>();
  }

  ngOnInit(): void {
    this.groupService.getAllGroups().subscribe({
      next: (groups) => {
        this.groups = groups;
      },
      error: (error) => {
        console.error('Error fetching groups:', error);
      }
    });

    this.filteredStockList = this.stockForm.get('name')!.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value || ''))
    );

    this.stockForm.get('name')!.valueChanges.subscribe(name => {
      const selectedStock = this.data.stockList.find(stock => stock.name === name);
      if (selectedStock) {
        this.stockForm.patchValue({
          symbol: selectedStock.symbol,
          exchange: selectedStock.exchange
        });
      } else {
        this.stockForm.patchValue({
          symbol: '',
          exchange: ''
        });
      }
    });
  }

  private _filter(value: string): { symbol: string; name: string; exchange: string }[] {
    const filterValue = value.toLowerCase();
    return this.data.stockList.filter(stock => stock.name.toLowerCase().includes(filterValue)).slice(0, 20);
  }

  displayFn(name?: string): string {
    return name ? name : '';
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.stockForm.valid) {
      const formData = this.stockForm.getRawValue();

      const purchaseDate = new Date(formData.purchaseDate);
      purchaseDate.setHours(1, 0, 0, 0); // Set to 1AM to avoid timezone issues
      const formattedDate = purchaseDate.toISOString().slice(0, 10);

      const stockData: Stock = {
        symbol: formData.symbol,
        name: formData.name,
        exchange: formData.exchange,
        quantity: formData.quantity,
        currentPrice: 0,
        price: formData.price,
        purchaseDate: formattedDate,
        groupId: formData.groupId,
      };

      if (this.isEditMode && this.stockSymbol) {
        this.stockService.updateStock(this.stockSymbol, stockData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => console.error('Error updating stock:', error)
        });
      } else {
        this.stockService.createStock(stockData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => console.error('Error creating stock:', error)
        });
      }
    }
  }
}
