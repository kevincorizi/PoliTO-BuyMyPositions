import {Component, OnInit} from '@angular/core';

declare function require(path: string);

@Component({
    selector: 'app-landing',
    templateUrl: './landing.component.html',
    styleUrls: ['./landing.component.scss']
})

// This component is used to display the homepage
export class LandingComponent implements OnInit {

    // Retrieve the developers pictures to be bound to the UI
    kevin = require('./assets/kevin.jpg');
    michelangelo = require('./assets/michelangelo.jpg');
    angelo = require('./assets/angelo.jpg');
    massimo = require('./assets/massimo.jpg');

    constructor() {
    }

    ngOnInit() {
    }

}
