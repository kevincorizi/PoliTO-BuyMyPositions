import {Component, OnInit} from '@angular/core';
import {saveAs} from 'file-saver/FileSaver';
import {ArchiveService} from '../../services/archive.service';
import {InputService} from '../../services/input.service';
import {Archive} from '../../models/archive';
import {MatDialog, MatSnackBar} from '@angular/material';
import {DialogComponent} from '../dialog/dialog.component';

@Component({
    selector: 'app-user-positions',
    templateUrl: './user-positions.component.html',
    styleUrls: ['./user-positions.component.scss']
})

// This component implements the owned positions view of the user page
export class UserPositionsComponent implements OnInit {

    // Owned archives to be displayed
    public archives: Archive[];

    constructor(
        private inputService: InputService,
        private archiveService: ArchiveService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) {
        this.archives = null;
    }

    ngOnInit() {
        this.getOwnArchives();
    }

    // Retrieve the owned archives and make them available to the view
    // While the archives are fetched, a loading bar is displayed in the view
    // to notify the user that something is actually happening
    public getOwnArchives() {
        this.archiveService.getUserOwnArchives().subscribe(
            (data: Archive[]) => {
                this.archives = data;
                // Notify the map that new archives are available for display
                this.inputService.pushArchives(data);
            }, (error) => {
                this.archives = new Array();
                // Show a small error bar on the bottom of the page
                this.snackBar.open('An error occurred fetching your archives: ' + error.status, '', {duration: 5000});
            }
        );
    }

    // Downloads an archive (including all of its positions) as a JSON file
    // id: the id of the archive to download
    downloadOwnArchive(id: string) {
        const filename = id + '.json';
        const archive = this.archives.find((a) => a.id === id);
        const blob = new Blob([JSON.stringify(archive, null, 2)], {type: 'application/json'});
        saveAs(blob, filename);
    }

    // Deletes an archive after asking for a confirmation
    // id: the id of the archive to be deleted
    deleteOwnArchive(id: string) {
        // Ask for confirmation
        let dialogRef = this.dialog.open(DialogComponent, {
            disableClose: true,
            autoFocus: true,
            data: {
                title: "Confirm delete",
                description: "If you delete the archive, it will not be available for new purchases. " +
                "It will remain visible for users that purchased it until now. " +
                "This operation cannot be undone. Do you want to proceed?"
            }
        });
        dialogRef.afterClosed().subscribe(
            data => {
                if (data) {
                    // The user confirmed
                    this.archiveService.deleteOwnArchive(id).subscribe(
                        (success) => {
                            // Archive is deleted from server, delete it from the local structures
                            const index = this.archives.indexOf(this.archives.find(a => a.id === id));
                            this.archives.splice(index, 1);
                            this.inputService.pushArchives(this.archives);
                            // Notify the user of the successful delete
                            this.snackBar.open('Archive deleted', '', {duration: 5000});
                        }, (error) => {
                            // Notify the user that something bad happened
                            this.snackBar.open('An error occurred deleting the archive: ' + error.status, '', {duration: 5000});
                        }
                    )
                }
            }
        )
    }
}
