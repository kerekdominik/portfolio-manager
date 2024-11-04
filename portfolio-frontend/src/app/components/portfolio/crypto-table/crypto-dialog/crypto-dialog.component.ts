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
import { CryptoService, Crypto } from '../../../services/crypto.service';

@Component({
  selector: 'app-crypto-dialog',
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
  templateUrl: './crypto-dialog.component.html',
  styleUrls: ['./crypto-dialog.component.css']
})
export class CryptoDialogComponent implements OnInit {
  groups: Group[] = [];
  cryptoForm: FormGroup;
  filteredCryptoList: Observable<{ id: string; name: string; symbol: string }[]>;
  isEditMode: boolean;
  cryptoId?: string;

  constructor(
    public dialogRef: MatDialogRef<CryptoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { element?: Crypto; cryptoList: { id: string; name: string; symbol: string }[] },
    private groupService: GroupService,
    private cryptoService: CryptoService
  ) {
    this.isEditMode = !!data.element;

    this.cryptoForm = new FormGroup({
      name: new FormControl('', Validators.required),
      id: new FormControl(''),
      symbol: new FormControl({ value: '', disabled: true }),
      quantity: new FormControl('', Validators.required),
      price: new FormControl('', Validators.required),
      purchaseDate: new FormControl(new Date(), Validators.required),
      groupId: new FormControl(''),
    });

    if (this.isEditMode && data.element) {
      this.cryptoId = data.element.id;
      this.cryptoForm.patchValue({
        name: data.element.name,
        id: data.element.id,
        symbol: data.element.symbol,
        quantity: data.element.quantity,
        price: data.element.price,
        purchaseDate: new Date(data.element.purchaseDate),
        groupId: data.element.groupId,
      });
    }

    this.filteredCryptoList = new Observable<{ id: string; name: string; symbol: string }[]>();
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

    this.filteredCryptoList = this.cryptoForm.get('name')!.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value || ''))
    );

    this.cryptoForm.get('name')!.valueChanges.subscribe(name => {
      const selectedCrypto = this.data.cryptoList.find(crypto => crypto.name === name);
      if (selectedCrypto) {
        this.cryptoForm.patchValue({
          id: selectedCrypto.id,
          symbol: selectedCrypto.symbol
        });
      } else {
        this.cryptoForm.patchValue({
          id: '',
          symbol: ''
        });
      }
    });
  }

  private _filter(value: string): { id: string; name: string; symbol: string }[] {
    const filterValue = value.toLowerCase();
    return this.data.cryptoList.filter(crypto => crypto.name.toLowerCase().includes(filterValue)).slice(0, 20);
  }

  displayFn(name?: string): string {
    return name ? name : '';
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.cryptoForm.valid) {
      const formData = this.cryptoForm.getRawValue();

      const purchaseDate = new Date(formData.purchaseDate);
      purchaseDate.setHours(1, 0, 0, 0); // Set to 1AM to avoid timezone issues
      const formattedDate = purchaseDate.toISOString().slice(0, 10);

      const cryptoData: Crypto = {
        id: formData.id,
        name: formData.name,
        symbol: formData.symbol,
        quantity: formData.quantity,
        currentPrice: 0,
        price: formData.price,
        purchaseDate: formattedDate,
        groupId: formData.groupId,
      };

      if (this.isEditMode && this.cryptoId) {
        this.cryptoService.updateCrypto(this.cryptoId, cryptoData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => console.error('Error updating crypto:', error)
        });
      } else {
        this.cryptoService.createCrypto(cryptoData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => console.error('Error creating crypto:', error)
        });
      }
    }
  }
}
