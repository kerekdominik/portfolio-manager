import { Component } from '@angular/core';
import {GroupService} from '../../../services/group-services.service';
import {FormsModule} from '@angular/forms';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';

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
    MatDialogActions
  ],
  templateUrl: './add-group.component.html',
  styleUrl: './add-group.component.css'
})
export class AddGroupComponent {
  groupName: string = '';

  constructor(
    private readonly groupService: GroupService,
    private readonly dialogRef: MatDialogRef<AddGroupComponent>
  ) {}

  addGroup() {
    if (this.groupName.trim()) {
      const newGroup = this.groupName;

      this.groupService.createGroup(newGroup).subscribe({
        next: (response) => {
          this.dialogRef.close(response);
        },
        error: (error) => {
          console.error('Error creating group:', error);
        }
      });
    }
  }

  onCancel() {

  }
}
