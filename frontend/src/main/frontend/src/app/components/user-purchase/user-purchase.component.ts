import {Component, OnInit} from '@angular/core';
import {InputService} from '../../services/input.service';
import {Polygon as LeafletPolygon} from 'leaflet';
import {Subscription} from 'rxjs/internal/Subscription';
import {ArchiveService} from '../../services/archive.service';
import {ApproxArchive, ArchiveListItem} from '../../models/approxarchive';
import {MatSnackBar, MatDialog} from '@angular/material';
import {PurchaseDialogComponent} from '../purchase-dialog/purchase-dialog.component';
import {UserListItem} from '../../models/user';
import {AuthenticationService} from '../../services/authentication.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-user-purchase',
    templateUrl: './user-purchase.component.html',
    styleUrls: ['./user-purchase.component.scss']
})

// This component implements the purchase view of the user page
export class UserPurchaseComponent implements OnInit {

    // Stores the time interval of the search
    public interval: Date[] = [];

    // Stored the current date, used as an upper bound for the time interval
    public now: Date;

    // Stores the polygon (received by the map) to provide the geographical area for the search
    public polygon: LeafletPolygon;
    public polygonSubscription: Subscription;

    // Stores approximate archives and corresponding users for the result of a search
    public archiveListItems: ArchiveListItem[];
    public userListItems: UserListItem[];

    // True if a search is in progress: used to show a loader on the page
    public loading: boolean;

    // True if at least one search occurred: used to hide search results from the view
    public searchOccurred: boolean;

    // User-to-color pairs to keep track of the color of positions and timestamps for a user
    private userColor: Map<string, string>;

    // True if the user deselected all the archives (or the users) of a search result
    // Used to avoid an empty purchase
    private purchaseIsDisabled: boolean;

    constructor(
        private snackBar: MatSnackBar,
        private inputService: InputService,
        private dialog: MatDialog,
        private archiveService: ArchiveService,
        private authService: AuthenticationService,
        private router: Router
    ) {
        this.polygon = undefined;
        this.now = new Date();

        this.archiveListItems = [];
        this.userListItems = [];
        this.loading = false;

        this.userColor = new Map();

        this.purchaseIsDisabled = false;
    }

    ngOnInit() {
        // Subscribe to the polygon events to receive updates from the map
        this.polygonSubscription = this.inputService.polygonSetUnsetEvents.subscribe((polygon) => {
            if (polygon) {
                this.polygon = polygon;
            } else {
                this.polygon = undefined;
            }
        });
    }

    ngOnDestroy() {
        // Unsubscribes from the updates from the map
        this.polygonSubscription.unsubscribe();
    }

    // Get the approximate archives when the search button is clicked
    getArchives() {
        // Clear local structures to be ready for the new results
        this.clearLocalCollections();
        this.archiveService.getApproxArchives(this.polygon, this.interval[0], this.interval[1]).subscribe(
            (approxArchives: ApproxArchive[]) => {
                // Successful request, display new data
                this.updateLocalCollections(approxArchives);
                // Pass the archives to the map to show them
                this.inputService.pushApproxArchives(this.archiveListItems);
            }, (error) => {
                // Failed request, show an error message on the bottom of the page
                this.snackBar.open('An error occurred fetching the archives: ' + error.status, '', {duration: 5000});
                this.loading = false;
            }
        );
    }

    // Handles the search button click
    onSubmit() {
        this.getArchives();
        this.searchOccurred = true;
        return;
    }

    // Empties all the local data structures
    clearLocalCollections() {
        this.archiveListItems = [];
        this.userListItems = [];
        this.inputService.pushApproxArchives([]);
        this.loading = true;
    }

    // Refreshes the local data after a successful search
    // archives: the approximate archives returned by the search query
    updateLocalCollections(archives: ApproxArchive[]) {
        let userMap: Map<string, number> = new Map();
        archives.forEach(archive => {
            // Insert a new item in the archives list
            this.archiveListItems.push({
                archive: archive,
                selected: true,
                color: this.getUserColor(archive.ownerUsername)
            });
            // Count the number of archives per user
            if (userMap.has(archive.ownerUsername)) {
                const currentArchives = userMap.get(archive.ownerUsername);
                userMap.set(archive.ownerUsername, currentArchives + 1);
            } else {
                userMap.set(archive.ownerUsername, 1);
            }
        });
        // Initialize the user list with the updated values
        userMap.forEach((value: number, key: string) => {
            this.userListItems.push({user: key, archives: value, selected: true, color: this.getUserColor(key)});
        });
        this.loading = false;
    }

    // Handles the click on a checkbox of the archive list
    // event: the click event
    // id: the of the clicked archive
    archiveStatusChanged(event: MouseEvent, id: string) {
        // Prevent checkbox clicks to open the accordion
        event.preventDefault();
        event.stopPropagation();
        // Get the clicked archive
        let clicked = this.archiveListItems.find((a) => a.archive.realArchiveId === id);
        // Get the owner
        let owner = this.userListItems.find((u) => u.user === clicked.archive.ownerUsername);

        if (clicked.selected) {
            // The click will de-select it
            clicked.selected = false;
            // If this is the last archive of a user, de-select the user
            owner.archives = owner.archives - 1;
            if (owner.archives === 0) {
                owner.selected = false;
            }
        } else {
            // The click will select it
            clicked.selected = true;
            // If this is the first selected archive for a user, select the user
            owner.archives = owner.archives + 1;
            if (owner.archives >= 1) {
                owner.selected = true;
            }
        }
        // Refresh the map with only the selected archives
        this.updateMap();
    }

    // Handles the clickof a checkbox of the user list
    // user: the username of the clicked user
    userStatusChanged(user: string) {
        // Get the user
        let clicked = this.userListItems.find((u) => u.user === user);
        if (clicked.selected) {
            // The click will de-select it
            clicked.selected = false;
            clicked.archives = 0;
            // Also de-select all the archives of the user
            this.archiveListItems.forEach((archive) => {
                if (archive.archive.ownerUsername === user) {
                    archive.selected = false;
                }
            });
        } else {
            // The click will select it
            clicked.selected = true;
            // Also select all the archives of the user
            this.archiveListItems.forEach((archive) => {
                if (archive.archive.ownerUsername === user) {
                    archive.selected = true;
                    clicked.archives++;
                }
            });
        }
        this.updateMap();
    }

    // Send updated archives to the map
    updateMap() {
        let archives = [];
        this.archiveListItems.forEach(a => {
            if (a.selected) {
                archives.push(a);
            }
        });
        this.inputService.pushApproxArchives(archives);
        if (archives.length == 0) {
            this.purchaseIsDisabled = true;
        } else {
            this.purchaseIsDisabled = false;
        }
    }

    // Handles the click on the purchase button
    purchase() {
        // Select only selected archives
        let toPurchase = [];
        this.archiveListItems.forEach(a => {
            if (a.selected) {
                toPurchase.push(a.archive);
            }
        });

        // Open the purchase dialog, providing the selected archives
        let dialogRef = this.dialog.open(PurchaseDialogComponent, {
            disableClose: true,
            autoFocus: true,
            data: {
                title: "Confirm purchase",
                description: "You are about to buy the following archives. Do you want to proceed?",
                doneTitle: "Cool!",
                doneDescription: "Here are your brand new archives. Download them now!",
                archives: toPurchase
            }
        });
        // After the dialog is closed, refresh the user balance to be updated with the previous transaction
        dialogRef.afterClosed().subscribe(() => {
            this.authService.updateBalance();
            this.router.navigate(['/user/purchases']);
        });
    }

    // Associates a random color to each user
    // user: the username to associate with a color
    getUserColor(user: string) {
        let userColor = "";
        if (this.userColor.has(user)) {
            userColor = this.userColor.get(user);
        } else {
            const r = Math.floor(Math.random() * 256);
            const g = Math.floor(Math.random() * 256);
            const b = Math.floor(Math.random() * 256);
            const color = "rgb(" + r + ", " + g + ", " + b + ")";
            this.userColor.set(user, color);
            userColor = color;
        }

        return userColor;
    }
}
