import {Component, OnInit, ViewEncapsulation} from '@angular/core';

// This component implements a wrapper for the user page
// The router outlet in the HTML allows to insert the position, upload, purchase and transactions
// sub-pages keeping the same base structure
@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class UserComponent implements OnInit {
    constructor() {
    }

    ngOnInit() {
    }
}
