// Class that implements a transaction
export class Transaction {
    // Transaction id
    id: string;

    // Username of the user that performed the transaction
    username: string;

    // Cost of the transaction
    amountPaid: number;

    // Ids of the archives included in the transaction
    archives: Array<any>;
}