package it.polito.gruppo2.mamk.lab3.controllers;


import it.polito.gruppo2.mamk.lab3.models.ApproxArchive;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealPosition;
import it.polito.gruppo2.mamk.lab3.services.archive.ArchiveService;
import it.polito.gruppo2.mamk.lab3.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/archives")
public class ArchiveController {

    @Autowired
    ArchiveService archiveService;

    @Autowired
    UserService userService;

    /**
     * Gets the approximated representations.
     *
     * @return a pair of timestamps and positions matching the polygon and timestamp
     */
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ApproxArchive> getApproximated(@RequestParam(name = "area") String area,
                                               @RequestParam(name = "from") Long from,
                                               @RequestParam(name = "to") Long to) {
        return archiveService.getApproximated(area, from, to);
    }

    /**
     * Returns all the archives uploaded by the user, not the ones bought
     *
     * @return List of uploaded archives
     */
    @RequestMapping(path = "/uploaded", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RealArchive> getAllUploaded() {
        return userService.getAllUploaded();
    }

    /**
     * Uploades a bunch of positions. It generates the double representation.
     */
    @RequestMapping(path = "/uploaded", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadPositions(@RequestBody List<RealPosition> positions) {
        archiveService.uploadPositions(positions);
    }


    /**
     * Returns a single archive between the ones uploaded by the user, not the ones bought
     *
     * @return Archive if exists, 404 if the archive does not exist or is not property of the user
     */
    @RequestMapping(path = "/uploaded/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RealArchive getUploadedbyId(@PathVariable("id") String id) {
        return userService.getUploadedbyId(id);
    }

    /**
     * Deletes a single archive between the ones uploaded by the user
     *
     * 200 if ok, 404 if not found or not property of the user.
     */
    @RequestMapping(path = "/uploaded/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUploadedbyId(@PathVariable("id") String id) {
        userService.deleteUploadedById(id);
    }

    /**
     * Returns all the archives bought by the user, not the ones uploaded
     *
     * @return List of uploaded archives
     */
    @RequestMapping(path = "/bought", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RealArchive> getAllBought() {
        return userService.getAllBought();
    }

    /**
     * Returns a single archive between the ones bought by the user, not the ones uploaded
     *
     * @return Archive if exists, 404 if the archive does not exist or is not property of the user
     */
    @RequestMapping(path = "/bought/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RealArchive getBoughtbyId(@PathVariable("id") String id) {
        return userService.getBoughtbyId(id);
    }


}
