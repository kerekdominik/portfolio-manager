import { Component } from '@angular/core';
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
import {EditItemDialogComponent} from '../edit-item-dialog/edit-item-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-groups-table',
  standalone: true,
  imports: [
    MatTable,
    MatHeaderCell,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatCell,
    MatIconButton,
    MatIcon,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatButton
  ],
  templateUrl: './groups-table.component.html',
  styleUrl: './groups-table.component.css'
})
export class GroupsTableComponent {
  displayedColumns: string[] = ['name', 'type', 'actions'];
  dataSource = [
    { name: 'Tech Stocks', type: 'Stock' },
    { name: 'Top Cryptos', type: 'Crypto' }
  ];

  constructor(public dialog: MatDialog) {
  }

  openEditDialog(element: any): void {
    const dialogRef = this.dialog.open(EditItemDialogComponent, {
      width: '300px',
      data: {
        ...element,
        fields: ['name', 'type']
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
    const index = this.dataSource.findIndex(item => item.name === element.name);
    if (index > -1) {
      this.dataSource.splice(index, 1);
      this.dataSource = [...this.dataSource];
    }
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(EditItemDialogComponent, {
      width: '300px',
      data: {
        name: '',
        type: '',
        fields: ['name', 'type']
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataSource = [...this.dataSource, result];
      }
    });
  }
}
