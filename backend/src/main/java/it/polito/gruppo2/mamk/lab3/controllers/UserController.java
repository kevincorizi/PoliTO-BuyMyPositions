package it.polito.gruppo2.mamk.lab3.controllers;

import it.polito.gruppo2.mamk.lab3.models.RegisteringUser;
import it.polito.gruppo2.mamk.lab3.persistence.user.User;
import it.polito.gruppo2.mamk.lab3.services.archive.ArchiveService;
import it.polito.gruppo2.mamk.lab3.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * This controller is in charge of managing the data relative to the user.
 * Here only the information relative to the user itself can be retrieved, so it does not manage buying.
 */
@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    UserService userService;


    /**
     * Gets information about the user
     *
     * @return all infos about the user
     */
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getProfile() {
        return userService.getProfile();
    }


    /**
     * Store the new user into db (only if form data is valid)
     */
    @RequestMapping(path = "/registration", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisteringUser newUser) {
        userService.createUser(newUser);
    }


    /**
     * Delete the user from the db
     */
    @RequestMapping(path = "/registration", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(){
        userService.deleteUser();
    }


    /**
     * Check the existence of an username passed as Query Parameter
     *
     * @return true if exists, false if it does not
     */
    @RequestMapping(path = "/checkExistence", method = RequestMethod.GET)
    public boolean checkExist(@RequestParam(value = "user") String username) {
        return userService.checkExistence(username);
    }
}
