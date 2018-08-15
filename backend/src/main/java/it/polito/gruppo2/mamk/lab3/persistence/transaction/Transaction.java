package it.polito.gruppo2.mamk.lab3.persistence.transaction;


import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.HashSet;
import java.util.Objects;

@Document()
public class Transaction {
    @Id
    private String id;

    @Indexed
    private String username; //User that performed the transaction

    private Long amountPaid;

    private Long timestamp; // When the transaction was performed

    private HashSet<String> archives; //Archives bought in this transaction

    public Transaction(String id, String username, Long amountPaid, Long timestamp, HashSet<String> archives) {
        this.id = id;
        this.username = username;
        this.amountPaid = amountPaid;
        this.timestamp = timestamp;
        this.archives = archives;
    }

    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Long amountPaid) {
        this.amountPaid = amountPaid;
    }

    public HashSet<String> getArchives() {
        if (archives == null)
            archives = new HashSet<String>();
        return archives;
    }

    public void setArchives(HashSet<String> archives) {
        this.archives = archives;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getAmountPaid(), that.getAmountPaid()) &&
                Objects.equals(getTimestamp(), that.getTimestamp()) &&
                Objects.equals(getArchives(), that.getArchives());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getUsername(), getAmountPaid(), getTimestamp(), getArchives());
    }
}
