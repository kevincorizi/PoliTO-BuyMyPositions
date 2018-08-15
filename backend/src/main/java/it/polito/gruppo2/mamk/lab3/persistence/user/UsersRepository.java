package it.polito.gruppo2.mamk.lab3.persistence.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
// By simply extending this interface, we inherit several crud methods and their implementation
// is instantiated on the fly by the framework when we run the application
public interface UsersRepository extends JpaRepository<User, String> {
    User findUserByUsername(String username);

    void deleteByUsername(String username);

    User save(User newUser);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} u set u.balance = u.balance + :balanceIncrement where u.username = :username")
    void incrementBalance(@Param("username") String username,
                          @Param("balanceIncrement") Long balanceIncrement);



}
