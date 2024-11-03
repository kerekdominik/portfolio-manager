import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule} from '@angular/material/input';
import { MatButtonModule} from '@angular/material/button';
import { MatSelectModule} from '@angular/material/select';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { debounceTime, map, Observable, startWith } from 'rxjs';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Group, GroupService } from '../../../services/group-services.service';

import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_NATIVE_DATE_FORMATS,
  MatNativeDateModule,
  NativeDateAdapter
} from '@angular/material/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-crypto-dialog',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
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
  tempData: any = {};
  cryptoControl = new FormControl('', Validators.required);
  dateControl = new FormControl(new Date(), Validators.required);
  filteredCryptoNames: Observable<string[]>;
  isEditMode: boolean;

  constructor(
    public dialogRef: MatDialogRef<CryptoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { element?: any; cryptoNames: string[] },
    private groupService: GroupService
  ) {
    this.isEditMode = !!data.element;
    if (this.isEditMode) {
      this.tempData = { ...data.element };
      this.cryptoControl.setValue(this.tempData.name);

      if (this.tempData.date) {
        this.dateControl.setValue(new Date(this.tempData.date));
      }
    }
    this.filteredCryptoNames = new Observable<string[]>();
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

    this.filteredCryptoNames = this.cryptoControl.valueChanges.pipe(
      debounceTime(100),
      startWith(''),
      map(value => this._filter(value || ''))
    );
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    if (!filterValue) {
      return [];
    }

    return this.data.cryptoNames
      .filter(name => name.toLowerCase().includes(filterValue))
      .slice(0, 20);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.cryptoControl.valid && this.dateControl.valid) {
      this.tempData.name = this.cryptoControl.value;
      this.tempData.date = this.dateControl.value;

      if (this.tempData.date) {
        this.tempData.date = this.tempData.date.toISOString();
      }

      this.dialogRef.close(this.tempData);
    }
  }
}
