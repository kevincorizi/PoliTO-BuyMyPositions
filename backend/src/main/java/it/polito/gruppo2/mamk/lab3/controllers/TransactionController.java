package it.polito.gruppo2.mamk.lab3.controllers;

import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;
import it.polito.gruppo2.mamk.lab3.persistence.transaction.Transaction;
import it.polito.gruppo2.mamk.lab3.services.Transaction.TransactionService;
import it.polito.gruppo2.mamk.lab3.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserService userService;

    /**
     * Buy the archives required in the id list.
     * This creates a Transaction, Updates the customer bought archives, updates the user and the customer balance.
     *
     * @return the archives that have been bought
     */
    @RequestMapping(path = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<RealArchive> buyPositions(@RequestBody() String[] archivesId) {
        return transactionService.buyPositions(Arrays.asList(archivesId));
    }

    /**
     * Returns all the transactions performed by the user
     *
     * @return Array of transactions.
     */

    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> getTransactions() {
        return userService.getTransactions();
    }
}
