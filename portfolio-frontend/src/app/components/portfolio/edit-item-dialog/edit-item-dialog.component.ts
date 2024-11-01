import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatOption, MatSelect} from '@angular/material/select';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {map, Observable, startWith} from 'rxjs';
import {MatAutocomplete, MatAutocompleteTrigger} from '@angular/material/autocomplete';

@Component({
  selector: 'app-edit-item-dialog',
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
    NgIf,
    ReactiveFormsModule,
    MatAutocompleteTrigger,
    MatAutocomplete,
    AsyncPipe
  ],
  templateUrl: './edit-item-dialog.component.html',
  styleUrl: './edit-item-dialog.component.css'
})
export class EditItemDialogComponent {
  groups = ['Top Cryptos', 'Altcoins', 'Stablecoins']; //TODO: get from API
  tempData: any;

  cryptoControl = new FormControl('');
  filteredCryptoNames: Observable<string[]>;

  constructor(
    public dialogRef: MatDialogRef<EditItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { fields: string[]; cryptoNames: string[] }
  ) {
    this.tempData = { ...data };
    this.filteredCryptoNames = new Observable<string[]>();
  }

  ngOnInit(): void {
    this.filteredCryptoNames = this.cryptoControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value || ''))
    );
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.data.cryptoNames.filter(crypto =>
      crypto.toLowerCase().includes(filterValue)
    );
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    this.tempData.name = this.cryptoControl.value;
    this.dialogRef.close(this.tempData); //TODO: review saving
  }

  isFieldVisible(field: string): boolean {
    return this.data.fields.includes(field);
  }
}
