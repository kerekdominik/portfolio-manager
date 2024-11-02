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
import {MatDialog} from '@angular/material/dialog';
import {AddGroupComponent} from './add-group/add-group.component';
import {Group, GroupService} from '../../services/group-services.service';

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
  displayedColumns: string[] = ['name', 'actions'];
  dataSource: Group[] = [];

  constructor(
    public dialog: MatDialog,
    private readonly groupService: GroupService
  ) {}

  ngOnInit(): void {
    this.loadGroups();
  }

  loadGroups(): void {
    this.groupService.getAllGroups().subscribe({
      next: (groups) => {
        this.dataSource = groups;
      },
      error: (error) => {
        console.error("Error fetching groups:", error);
      }
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(AddGroupComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadGroups(); // Reload groups after adding a new one
      }
    });
  }

  openEditDialog(element: Group): void {
    const dialogRef = this.dialog.open(AddGroupComponent, {
      width: '400px',
      data: { id: element.id, name: element.name }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadGroups(); // Reload groups after editing
      }
    });
  }

  delete(element: Group): void {
    this.groupService.deleteGroup(element.id).subscribe({
      next: () => {
        this.loadGroups(); // Reload groups after deleting
      },
      error: (error) => {
        console.error("Error deleting group:", error);
      }
    });
  }
}
