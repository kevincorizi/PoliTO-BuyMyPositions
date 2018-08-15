package it.polito.gruppo2.mamk.lab3.services.user;

import it.polito.gruppo2.mamk.lab3.models.RegisteringUser;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;
import it.polito.gruppo2.mamk.lab3.persistence.transaction.Transaction;
import it.polito.gruppo2.mamk.lab3.persistence.user.User;

import java.util.List;

/**
 * This service is the one implementing the business logic related to the user.
 * It is not used by Spring security, therefore it is a separate service from UserDetail
 */
public interface UserService {
    /**
     * Gets information about the user
     *
     * @return all infos about the user
     */
    User getProfile();

    /**
     * Store the new user into db (only if form data is valid)
     */
    void createUser(RegisteringUser newUser);

    /**
     * Delete the user invoking this method
     */
    void deleteUser();

    /**
     * Check the existence of an username passed as Query Parameter
     *
     * @return true if exists, false if it does not
     */
    boolean checkExistence(String username);

    /**
     * Returns all the archives uploaded by the user, not the ones bought
     *
     * @return List of uploaded archives
     */
    List<RealArchive> getAllUploaded();

    /**
     * Returns a single archive between the ones uploaded by the user, not the ones bought
     *
     * @return Archive if exists, 404 if the archive does not exist or is not property of the user
     */
    RealArchive getUploadedbyId(String id);

    /**
     * Returns all the archives bought by the user, not the ones uploaded
     *
     * @return List of uploaded archives
     */
    List<RealArchive> getAllBought();

    /**
     * Returns a single archive between the ones bought by the user, not the ones uploaded
     *
     * @return Archive if exists, 404 if the archive does not exist or is not property of the user
     */
    RealArchive getBoughtbyId(String id);

    /**
     * Returns all the transactions performed by the user
     *
     * @return Array of transactions.
     */
    List<Transaction> getTransactions();

    /**
     * Deletes a single archive between the ones uploaded by the user
     *
     * @return 200 if ok, 404 if not found or not property of the user.
     */
    void deleteUploadedById(String id);


}
