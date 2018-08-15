import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {AuthenticationService} from '../../services/authentication.service';
import {throwError} from 'rxjs';
import {Router} from '@angular/router';

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html',
    styleUrls: ['./signup.component.scss'],
    encapsulation: ViewEncapsulation.None
})

// This component implements the registration page
export class SignupComponent implements OnInit {

    // User input field (used with NgModel binding)
    public username: string;

    // Password input field (used with NgModel binding)
    public password: string;

    // Repeat password input field (used with NgModel binding)
    public confirmpassword: string;

    // Password validation: at least one number
    private passwordRegex: RegExp = new RegExp('^(?=.*\\d).{8,}$');

    // Username validation: no special characters or spaces
    private usernameRegex: RegExp = new RegExp('^[a-zA-Z0-9_\\.\\-]+$');

    // Error state fields used to display a custom error message, if any
    public error: boolean;
    public errorMessage: string;

    constructor(
        private authService: AuthenticationService,
        private router: Router
    ) {
        this.error = false;
        this.errorMessage = '';
    }

    ngOnInit() {
    }

    onSubmit() {
        // Validate the username
        if (!this.usernameRegex.test(this.username)) {
            this.error = true;
            this.errorMessage = 'Your username should only contain letters, numbers, . _';
            return;
        }
        // Validate the password
        if (!this.passwordRegex.test(this.password)) {
            this.error = true;
            this.errorMessage = 'Your password must contain at least 8 characters and one number';
            return;
        }
        // Check password mismatch
        if (this.password !== this.confirmpassword) {
            this.error = true;
            this.errorMessage = 'Passwords are not equal.<br/>Check them and try again.';
            return;
        }
        // Check for duplicate usernames
        this.authService.checkUsername(this.username).subscribe(
            (result) => {
                // The username already exists
                if (result) {
                    this.error = true;
                    this.errorMessage = 'The selected username is already taken.<br/>Try another one!';
                    return;
                }
                // The username does not exist, proceed with registration
                this.authService.register(this.username, this.password)
                    .catch(
                        (error: any) => {
                            return throwError(error.statusText);
                        }
                    ).subscribe(
                        (success) => {
                            // If the registration succeeded, redirect to the login page
                            this.router.navigate(['/login'], {queryParams: {ref: 'registration'}});
                        }, (error) => {
                            this.error = true;
                            this.errorMessage = error;
                        }
                    );
            }
        );
    }

    clearError() {
        this.error = false;
        this.errorMessage = '';
    }

    // Check for duplicate username when the user types his username
    checkUsername() {
        if (this.username !== '') {
            if (this.usernameRegex.test(this.username)) {
                this.authService.checkUsername(this.username)
                    .subscribe(
                        (result) => {
                            if (result) {
                                this.error = true;
                                this.errorMessage = 'The selected username is already taken.<br/>Try another one!';
                            }
                        }
                    );
            } else {
                this.error = true;
                this.errorMessage = 'Your username should only contain letters, numbers, . _';
            }
        }
    }

    // Validate the password when the user types is
    checkPassword() {
        if (!this.passwordRegex.test(this.password)) {
            this.error = true;
            this.errorMessage = 'Your password must contain at least 8 characters and one number';
            return;
        }
    }
}
