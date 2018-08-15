package it.polito.gruppo2.mamk.lab3.services.Transaction;

import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;

import java.util.List;

public interface TransactionService {
    /**
     * Buy the archives required in the id list.
     * This creates a Transaction, Updates the customer bought archives, updates the user and the customer balance.
     *
     * @return the archives that have been bought
     */
    List<RealArchive> buyPositions(List<String> archivesId);
}
