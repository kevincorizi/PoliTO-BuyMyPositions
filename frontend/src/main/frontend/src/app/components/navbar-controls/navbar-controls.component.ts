import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from '../../services/authentication.service';
import {Router, NavigationEnd} from '@angular/router';

@Component({
    selector: 'app-navbar-controls',
    templateUrl: './navbar-controls.component.html',
    styleUrls: ['./navbar-controls.component.scss']
})

// This component implements the main navbar of the website
export class NavbarControlsComponent implements OnInit {

    // Navbar title
    public title = 'BuyMyPositions';

    // Current page is the user page: used to change the menu
    private isUserPage: boolean;

    // Current page is the login page: used to change the menu
    private isLoginPage: boolean;

    // Navbar css class (used bound to NgClass)
    private class: string;

    constructor(
        public authService: AuthenticationService,
        public router: Router
    ) {
    }

    ngOnInit() {
        if (this.authService.getBalance() == -1) {
            this.authService.updateBalance();
        }
        this.router.events.subscribe((val) => {
            if (val instanceof NavigationEnd) {
                this.isUserPage = val.urlAfterRedirects.startsWith('/user');
                this.isLoginPage = val.urlAfterRedirects.startsWith('/login');
                this.class = this.getClass(val.urlAfterRedirects);
            }
        })
    }

    // url: the current url
    public getClass(url: string) {
        if (url !== '/' && url !== '/landing') {
            return 'bmp-navbar';
        } else {
            return 'bmp-navbar bmp-navbar-transparent';
        }
    }

    logout() {
        this.authService.logOut()
            .subscribe(
                (ok) => {
                    this.router.navigate(['/']);
                },
                (err) => {
                    this.router.navigate(['/']);
                }
            );
    }
}
