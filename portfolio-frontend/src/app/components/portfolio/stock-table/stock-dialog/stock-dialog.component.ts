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
  stockId?: string;

  constructor(
    public dialogRef: MatDialogRef<StockDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { element?: Stock; stockList: { symbol: string; name: string; exchange: string }[] },
    private readonly groupService: GroupService,
    private readonly stockService: StockService
  ) {
    this.isEditMode = !!data.element;

    this.stockForm = new FormGroup({
      name: new FormControl('', Validators.required),
      id: new FormControl(''),
      symbol: new FormControl({ value: '', disabled: true }),
      exchange: new FormControl({ value: '', disabled: true }),
      quantity: new FormControl('', Validators.required),
      price: new FormControl('', Validators.required),
      purchaseDate: new FormControl(new Date(), Validators.required),
      groupId: new FormControl(''),
    });

    if (this.isEditMode && data.element) {
      this.stockId = data.element.id;
    }

    this.filteredStockList = new Observable<{ symbol: string; name: string; exchange: string }[]>();
  }

  ngOnInit(): void {
    this.groupService.getAllGroups().subscribe({
      next: (groups) => {
        this.groups = groups;

        if (this.isEditMode && this.data.element) {
          const selectedGroup = this.data.element.groupName
            ? this.groups.find(g => g.name === this.data.element!.groupName)
            : undefined;

          this.stockForm.patchValue({
            name: this.data.element.name,
            id: this.data.element.id,
            symbol: this.data.element.symbol,
            exchange: this.data.element.exchange,
            quantity: this.data.element.quantity,
            price: this.data.element.price,
            purchaseDate: new Date(this.data.element.purchaseDate),
            groupId: selectedGroup ? selectedGroup.id : ''
          });
        }
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
        }, {emitEvent: false});
      } else {
        this.stockForm.patchValue({
          symbol: '',
          exchange: ''
        }, {emitEvent: false});
      }
    });
  }

  private _filter(value: string): { symbol: string; name: string; exchange: string }[] {
    const filterValue = value.toLowerCase();
    return this.data.stockList.filter(stock => stock.name.toLowerCase().includes(filterValue)).slice(0, 20);
  }

  displayFn(name?: string): string {
    return name ?? '';
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
        id: formData.id,
        symbol: formData.symbol,
        name: formData.name,
        exchange: formData.exchange,
        quantity: formData.quantity,
        currentPrice: 0,
        price: formData.price,
        purchaseDate: formattedDate,
        groupId: formData.groupId,
      };

      if (this.isEditMode && this.stockId) {
        this.stockService.updateStock(this.stockId, stockData).subscribe({
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
