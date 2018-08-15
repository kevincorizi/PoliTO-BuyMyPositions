package it.polito.gruppo2.mamk.lab3.persistence.transaction;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> getTransactionsByUsername(String username);
}
