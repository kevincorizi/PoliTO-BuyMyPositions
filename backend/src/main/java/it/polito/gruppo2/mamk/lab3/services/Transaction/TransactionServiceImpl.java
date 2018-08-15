package it.polito.gruppo2.mamk.lab3.services.Transaction;

import it.polito.gruppo2.mamk.lab3.exceptions.BadRequestException;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchiveRepository;
import it.polito.gruppo2.mamk.lab3.persistence.transaction.Transaction;
import it.polito.gruppo2.mamk.lab3.persistence.transaction.TransactionRepository;
import it.polito.gruppo2.mamk.lab3.persistence.user.User;
import it.polito.gruppo2.mamk.lab3.persistence.user.UsersRepository;
import it.polito.gruppo2.mamk.lab3.services.BasicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class TransactionServiceImpl extends BasicServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    RealArchiveRepository realArchiveRepository;

    @Autowired
    UsersRepository usersRepository;

    @Override
    @Transactional
    public List<RealArchive> buyPositions(List<String> archivesId) {
        String username = getUsername();
        User user = usersRepository.findUserByUsername(username);

        List<RealArchive> archivesToBuy = new ArrayList<RealArchive>();
        long totalAmountToPay = 0L; // Will be increased in the next loop, based on how much each user is payed

        for (String id : archivesId) {
            // Validating the request
            if (user.getBoughtArchives().contains(id))
                throw new BadRequestException("You already bought the archive " + id);

            RealArchive archive = realArchiveRepository.getRealArchivesById(id);
            if (archive.getOwnerUsername().equals(username))
                throw new BadRequestException("You uploaded the archive " + id);

            archivesToBuy.add(archive);

            // Let's just suppose 1 position costs 1 unit
            long amount = archive.getPositions().size();
            totalAmountToPay += amount;
        }

        if (user.getBalance() < totalAmountToPay)
            throw new BadRequestException("You need " + (totalAmountToPay - user.getBalance()) + " to afford all these positions.");

        // Ok everything looks fine. Let's go with the transaction

        // First, we take the money from the user
        user.setBalance(user.getBalance() - totalAmountToPay);

        // We pay the other users
        long amount = 0;
        for (RealArchive archive : archivesToBuy) {
            amount = archive.getPositions().size();
            payUser(archive.getOwnerUsername(), amount);
        }

        // We increase the number of times the archive has been bought
        for (RealArchive archive : archivesToBuy) {
            archive.setTimesBought(archive.getTimesBought()+1);
            realArchiveRepository.save(archive);
        }

        // We assign the archives to the user and save it
        user.getBoughtArchives().addAll(archivesId);

        usersRepository.save(user);

        // We create the transaction
        Transaction transaction = new Transaction();
        transaction.setUsername(getUsername());
        transaction.setArchives(new HashSet<>(archivesId));
        transaction.setAmountPaid(totalAmountToPay);
        transaction.setTimestamp(new Date().getTime());

        transactionRepository.save(transaction);

        return archivesToBuy;
    }

    private void payUser(String username, long amount){
        User userToPay = usersRepository.findUserByUsername(username);
        userToPay.setBalance(userToPay.getBalance() + amount);
        usersRepository.save(userToPay);
    }
}
