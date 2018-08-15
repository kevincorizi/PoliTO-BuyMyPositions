import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Transaction} from '../models/transaction';
import {Archive} from '../models/archive';

@Injectable({
    providedIn: 'root'
})

// This service handles transaction-related requests
export class TransactionService {

    // Transaction URL: used to retrieve and add transactions
    private transactionsURL: string = '/api/transactions';

    constructor(private http: HttpClient) {
    }

    getUserTransactions(): Observable<Transaction[]> {
        const apiUrl = this.transactionsURL;
        return this.http.get<Transaction[]>(apiUrl);
    }

    // archives: array of archive IDs to be bought as part of the transaction
    addTransaction(archives: string[]): Observable<any> {
        const apiUrl = this.transactionsURL;
        return this.http.post<Archive[]>(apiUrl, archives);
    }
}
