import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';

@Component({
    selector: 'app-dialog',
    templateUrl: './dialog.component.html',
    styleUrls: ['./dialog.component.scss']
})

// This component implements a generic dialog that can show a title and a message
// In this project it is used as a confirmation dialog upon archive deletion
// It is based on the Angular Material dialog: refer to https://material.angular.io/components/dialog/overview 
export class DialogComponent implements OnInit {
    title: string;
    description: string;

    constructor(
        private dialogRef: MatDialogRef<DialogComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        this.title = data.title;
        this.description = data.description;
    }

    ngOnInit() {
    }

    yes() {
        this.dialogRef.close(true);
    }

    no() {
        this.dialogRef.close(false);
    }
}
