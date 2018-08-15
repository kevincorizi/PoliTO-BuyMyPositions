import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {ApproxArchive} from '../../models/approxarchive';
import {TransactionService} from '../../services/transaction.service';
import {Archive} from '../../models/archive';
import {saveAs} from 'file-saver/FileSaver';

@Component({
    selector: 'app-purchase-dialog',
    templateUrl: './purchase-dialog.component.html',
    styleUrls: ['./purchase-dialog.component.scss']
})

// This component implements a dialog that can show a list of entries and perform an HTTP request
// In this project it is used as a confirmation dialog upon purchase: it displays
// the archives the user is about to buy, asks for confirmation, performs the transaction
// and displays the result.
// It is based on the Angular Material dialog: refer to https://material.angular.io/components/dialog/overview 
export class PurchaseDialogComponent implements OnInit {

    // Dialog title
    title: string;

    // Dialog title after the request
    doneTitle: string;

    // Dialog text
    description: string;

    // Dialog text after the request
    doneDescription: string;

    // Approximate archives to be displayed
    archives: ApproxArchive[];

    // True during the request: used to display a circle loader
    loading: boolean;

    // True after the request
    done: boolean;

    // True if an error occurred during the request
    error: boolean;
    errorCode: number;

    // Real archives purchased, returned by the server after a successful request
    purchaseArchives: Archive[];

    constructor(
        private transactionService: TransactionService,
        private dialogRef: MatDialogRef<PurchaseDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data
    ) {
        this.title = data.title;
        this.doneTitle = data.doneTitle;
        this.description = data.description;
        this.doneDescription = data.doneDescription;
        this.archives = data.archives;

        this.loading = false;
        this.done = false;
        this.error = false;
        this.errorCode = 0;
    }

    ngOnInit() {
    }

    // Perform purchase
    purchase() {
        this.loading = true;
        this.done = false;
        this.error = false;

        // Collect the archive ids of the to-be-purchased archives
        let archiveIds = [];
        this.archives.forEach((a) => {
            archiveIds.push(a.realArchiveId);
        });

        // Send the request
        this.transactionService.addTransaction(archiveIds).subscribe(
            (data: Archive[]) => {
                // Successful request: update local data
                this.purchaseArchives = data;
                this.done = true;
                this.loading = false;
            }, (err) => {
                // Failed request: update local data
                this.done = true;
                this.loading = false;
                this.error = true;
                this.errorCode = err.status;
            }
        );

    }

    close() {
        this.dialogRef.close();
    }

    // Download the purchased archives after a successful request
    downloadPurchase() {
        const filename = 'purchase_' + new Date().getTime() + '.json';
        const blob = new Blob([JSON.stringify(this.purchaseArchives, null, 2)], {type: 'application/json'});
        saveAs(blob, filename);
    }
}
