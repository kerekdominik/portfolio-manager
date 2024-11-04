import {Component, Inject} from '@angular/core';
import {GroupService} from '../../../services/group.service';
import {FormsModule} from '@angular/forms';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-add-group',
  standalone: true,
  imports: [
    FormsModule,
    MatFormField,
    MatInput,
    MatButton,
    MatLabel,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    NgIf
  ],
  templateUrl: './group-dialog.component.html',
  styleUrl: './group-dialog.component.css'
})
export class GroupDialogComponent {
  groupName: string = '';
  isEditMode: boolean = false;
  groupId?: number;

  constructor(
    private readonly groupService: GroupService,
    private readonly dialogRef: MatDialogRef<GroupDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    if (data) {
      this.isEditMode = true;
      this.groupName = data.name;
      this.groupId = data.id;
    }
  }

  saveGroup() {
    if (this.isEditMode && this.groupId != null) {
      this.groupService.updateGroup(this.groupId, this.groupName).subscribe({
        next: () => this.dialogRef.close({ name: this.groupName, id: this.groupId }),
        error: (error) => console.error('Error updating group:', error)
      });
    } else {
      this.groupService.createGroup(this.groupName).subscribe({
        next: (createdGroup) => this.dialogRef.close(createdGroup),
        error: (error) => console.error('Error creating group:', error)
      });
    }
  }

  deleteGroup() {
    if (this.groupId != null) {
      this.groupService.deleteGroup(this.groupId).subscribe({
        next: () => this.dialogRef.close({ delete: true, id: this.groupId }),
        error: (error) => console.error('Error deleting group:', error)
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
