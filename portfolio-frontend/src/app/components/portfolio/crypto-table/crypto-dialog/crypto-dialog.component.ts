import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatError, MatFormField, MatLabel} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatOption, MatSelect } from '@angular/material/select';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {debounceTime, map, Observable, startWith} from 'rxjs';
import { MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { ScrollingModule } from '@angular/cdk/scrolling';
import {Group, GroupService} from '../../../services/group-services.service';

@Component({
  selector: 'app-crypto-dialog',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormField,
    MatInput,
    FormsModule,
    MatDialogActions,
    MatButton,
    MatLabel,
    MatSelect,
    MatOption,
    NgForOf,
    ReactiveFormsModule,
    MatAutocompleteTrigger,
    MatAutocomplete,
    AsyncPipe,
    ScrollingModule,
    NgIf,
    MatError
  ],
  templateUrl: './crypto-dialog.component.html',
  styleUrl: './crypto-dialog.component.css'
})
export class CryptoDialogComponent implements OnInit {
  groups: Group[] = [];
  tempData: any = {};
  cryptoControl = new FormControl('', Validators.required);
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
    }
    this.filteredCryptoNames = new Observable<string[]>();
  }

  ngOnInit(): void {
    this.groupService.getAllGroups().subscribe({
      next: (groups) => {
        this.groups = groups;
      },
      error: (error) => {
        console.error("Error fetching groups:", error);
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
    if (this.cryptoControl.valid) {
      this.tempData.name = this.cryptoControl.value;
      this.dialogRef.close(this.tempData);
    }
  }
}
