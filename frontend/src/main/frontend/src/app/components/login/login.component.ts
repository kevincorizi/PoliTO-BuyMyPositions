import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {AuthenticationService} from '../../services/authentication.service';
import 'rxjs/add/operator/catch';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    encapsulation: ViewEncapsulation.None
})

// This component implements the login page
export class LoginComponent implements OnInit {

    // User input field (used with NgModel binding)
    username: string;

    // Password input field (used with NgModel binding)
    password: string;

    // Error state fields used to display a custom error message, if any
    error: boolean;
    errorMessage: string;

    // Used to change the login message if the user has just registered
    fromRegistration: boolean;

    constructor(
        private authService: AuthenticationService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.error = false;
        this.route.queryParams.subscribe(params => {
            if (params['ref'] && params['ref'] == 'registration') {
                this.fromRegistration = true;
            } else {
                this.fromRegistration = false;
            }
        });
    }

    ngOnInit() {

    }

    onSubmit() {
        this.authService.logIn(this.username, this.password)
            .subscribe(
                (success) => {
                    this.router.navigate(['/user'])
                },
                (error) => {
                    this.error = true;
                    this.errorMessage = error;
                }
            );
    }

    clearError() {
        this.error = false;
        this.errorMessage = "";
    }
}
