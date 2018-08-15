import {Component, OnInit} from '@angular/core';
import {Observable, from} from 'rxjs';
import {Transaction} from '../../models/transaction';
import {TransactionService} from '../../services/transaction.service';
import {Archive} from '../../models/archive';
import {mergeMap, toArray} from 'rxjs/operators';
import {ArchiveService} from '../../services/archive.service';
import {saveAs} from 'file-saver/FileSaver';
import {MatSnackBar} from '@angular/material';

@Component({
    selector: 'app-user-purchases',
    templateUrl: './user-purchases.component.html',
    styleUrls: ['./user-purchases.component.scss']
})

// This component implements the transactions view of the user page
export class UserPurchasesComponent implements OnInit {

    // Transactions to be displayed
    public transactions: Transaction[];

    // Archives in each transaction
    // This map is used as a cache for HTTP requests results.
    // By default a transaction result only contains archive IDs, so if
    // the user wants detailed data about an archive a separate request is needed.
    // If a user clicks on a transaction to show its archives for the first time,
    // an HTTP request is performed and the observable result is stored
    // to prevent bogus requests.
    // Note that this is possible because archives are immutable.
    public transactionArchives: Map<string, Observable<Archive[]>>;

    constructor(
        private transactionService: TransactionService,
        private archiveService: ArchiveService,
        private snackBar: MatSnackBar
    ) {
        this.transactions = null;
        this.transactionArchives = new Map();
    }

    ngOnInit() {
        this.getTransactions();
    }

    // Retrieve the transactions of the user
    // While the transactions are fetched, a loading bar is displayed in the view
    // to notify the user that something is actually happening
    public getTransactions() {
        this.transactionService.getUserTransactions().subscribe(
            (data: Transaction[]) => {
                this.transactions = data;
            }, (error) => {
                // Notify the error
                this.transactions = new Array();
                this.snackBar.open('An error occurred fetching your transactions: ' + error.status, '', {duration: 5000});
            }
        );
    }

    // Retrieves the details of the archives in the clicked transaction
    // tid: the transaction id
    // ids: the ids of the archives of the transaction
    public getArchivesForTransaction(tid: string, ids: string[]): Observable<Archive[]> {
        // Exploit the caching mechanism: if the archives already exist, do not
        // perform an HTTP request
        if (this.transactionArchives.has(tid)) {
            return this.transactionArchives.get(tid);
        }
        // Fetch all the archives of the transaction, keeping the order
        // in which they appear in the transaction
        const ret: Observable<Archive[]> = from(ids).pipe(
            mergeMap((id) => {
                return this.archiveService.getBoughtArchive(id);
            }),
            toArray()
        );
        // Update the local cache
        this.transactionArchives.set(tid, ret);
        return ret;
    }

    // Download a single archive in a transaction
    // archive: the archive to be downloaded as a JSON file
    public downloadArchive(archive: Archive) {
        const filename = archive.id + '.json';
        const blob = new Blob([JSON.stringify(archive, null, 2)], {type: 'application/json'});
        saveAs(blob, filename);
    }

    // Download a transaction including the detail of all its archives
    // transaction: the transaction to be downloaded as a JSON file
    // archives: the archives to be included in the downloaded JSON file
    public downloadTransaction(transaction: Transaction, archives: Archive[]) {
        const filename = transaction.id + '.json';
        transaction.archives = archives;
        const blob = new Blob([JSON.stringify(transaction, null, 2)], {type: 'application/json'});
        saveAs(blob, filename);
    }
}
