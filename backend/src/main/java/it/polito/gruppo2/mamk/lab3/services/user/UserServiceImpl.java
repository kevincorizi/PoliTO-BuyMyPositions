package it.polito.gruppo2.mamk.lab3.services.user;

import it.polito.gruppo2.mamk.lab3.exceptions.ArchiveNotFoundException;
import it.polito.gruppo2.mamk.lab3.exceptions.ExistingUsernameException;
import it.polito.gruppo2.mamk.lab3.exceptions.InvalidRegistrationException;
import it.polito.gruppo2.mamk.lab3.models.RegisteringUser;
import it.polito.gruppo2.mamk.lab3.persistence.approxarchive.ApproxPositionArchiveRepository;
import it.polito.gruppo2.mamk.lab3.persistence.approxarchive.ApproxTimestampArchiveRepository;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchiveRepository;
import it.polito.gruppo2.mamk.lab3.persistence.transaction.Transaction;
import it.polito.gruppo2.mamk.lab3.persistence.transaction.TransactionRepository;
import it.polito.gruppo2.mamk.lab3.persistence.user.User;
import it.polito.gruppo2.mamk.lab3.persistence.user.UsersRepository;
import it.polito.gruppo2.mamk.lab3.services.BasicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends BasicServiceImpl implements UserService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RealArchiveRepository realArchiveRepository;
    @Autowired
    ApproxPositionArchiveRepository approxPositionArchiveRepository;
    @Autowired
    ApproxTimestampArchiveRepository approxTimestampArchiveRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PasswordEncoder userPasswordEncoder;

    @Override
    @PreAuthorize("hasRole('USER')")
    public User getProfile() {
        return usersRepository.findUserByUsername(getUsername());
    }


    @Transactional
    public void createUser(RegisteringUser newUser) {
        // Check both fields have a non null value
        if(newUser.getUsername() == null || newUser.getPassword() == null){
            throw new InvalidRegistrationException("You must provide both a Username and a Password!");
        }

        // Check username constraints
        if (!newUser.getUsername().matches("[a-zA-Z0-9_.-]+")) {
            throw new InvalidRegistrationException("The username may contain only letters, numbers, and the symbols . - _");
        }

        // Check password constraint
        if (!newUser.getPassword().matches("(?=.*\\d).{8,}")) {
            throw new InvalidRegistrationException("The provided password is not valid!");
        }

        // Check username uniqueness
        User user = usersRepository.findUserByUsername(newUser.getUsername());
        if (user != null) {
            throw new ExistingUsernameException("Username already exists!");
        }

        it.polito.gruppo2.mamk.lab3.persistence.user.User persistenceUser = new User();

        persistenceUser.setRole("USER");
        persistenceUser.setPassword(userPasswordEncoder.encode(newUser.getPassword()));
        persistenceUser.setUsername(newUser.getUsername());
        usersRepository.save(persistenceUser);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void deleteUser(){
        usersRepository.deleteByUsername(getUsername());
    }

    @Transactional(readOnly = true)
    public boolean checkExistence(String username) {
        User user = usersRepository.findUserByUsername(username);
        return user != null;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public List<RealArchive> getAllUploaded() {
        return realArchiveRepository.getRealArchivesByOwnerUsernameAndCanBeBoughtIsTrue(getUsername());
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public RealArchive getUploadedbyId(String id) {
        RealArchive archive = realArchiveRepository.getRealArchivesByIdAndOwnerUsernameAndCanBeBoughtIsTrue(id, getUsername());
        if (archive == null)
            throw new ArchiveNotFoundException();
        return archive;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public List<RealArchive> getAllBought() {
        User user = usersRepository.findUserByUsername(getUsername());
        return realArchiveRepository.getRealArchivesByIdIn(user.getBoughtArchives());
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public RealArchive getBoughtbyId(String id) {
        User user = usersRepository.findUserByUsername(getUsername());
        if (!user.getBoughtArchives().contains(id)) {
            throw new ArchiveNotFoundException();
        }
        RealArchive archive = realArchiveRepository.getRealArchivesById(id);
        if (archive == null)
            throw new ArchiveNotFoundException();
        return archive;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public List<Transaction> getTransactions() {
        return transactionRepository.getTransactionsByUsername(getUsername());
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void deleteUploadedById(String id) {
        // We don't want users eliminating random archives :-)
        RealArchive archive = realArchiveRepository.getRealArchivesByIdAndOwnerUsernameAndCanBeBoughtIsTrue(id, getUsername());
        if (archive == null)
            throw new ArchiveNotFoundException();
        if (archive.getTimesBought().longValue() == 0) {
            realArchiveRepository.removeById(id);
            approxPositionArchiveRepository.removeByOriginalArchive(id);
            approxTimestampArchiveRepository.removeByOriginalArchive(id);
        }else{
            archive.setCanBeBought(false);
            realArchiveRepository.save(archive);
        }

    }
}
