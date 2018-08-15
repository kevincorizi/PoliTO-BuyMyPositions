import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import 'rxjs/add/operator/map';
import {JwtHelperService} from '@auth0/angular-jwt';
import {StorageService} from './storage.service';
import {Observable} from 'rxjs/Observable';
import {throwError, empty} from 'rxjs';

@Injectable({
    providedIn: 'root'
})

// This service handles authentication-related requests
export class AuthenticationService {

    // Base API url
    private apiURL = '';

    // Authorization URL: used in the first step of the OAuth flow
    private authorizeURL = this.apiURL + '/oauth/authorize';

    // Token URL: used in the second step of the OAuth flow
    private tokenURL = this.apiURL + '/oauth/token';

    // Logout URL: used to perform logout
    private logoutURL = this.apiURL + '/oauth/logout';

    // Registration URL
    private registerURL = this.apiURL + '/api/user/registration';

    // User existence URL: used during registration to avoid duplicate usernames
    private checkExistenceURL = this.apiURL + '/api/user/checkExistence';

    // User profile URL: used to get user data (name, balance)
    private profileURL = this.apiURL + '/api/user';

    // Fields needed to store and parse the OAuth JWT token
    private tokenStorageKey = 'jwt_token';
    private jwtHelper: JwtHelperService = new JwtHelperService();

    // Current balance: displayed and updated after each transaction
    private currentBalance: number = -1;

    constructor(
        private http: HttpClient,
        private storageService: StorageService
    ) {
    }

    // Returns true if the user is logged
    public isLogged(): boolean {
        const token = this.getRawToken();
        return token && !this.jwtHelper.isTokenExpired(token);
    }

    // Returns true if the level of privilege of the current user is USER
    // This allows for future multi-privilege users (admin, customer-only...)
    public isUser(): boolean {
        return this.getParsedToken().authorities.includes('ROLE_USER');
    }

    // Performs login using the OAuth Authorization Code flow
    public logIn(username: string, password: string): Observable<any> {
        const optionsGet = {
            headers: {
                'Authorization': 'Basic ' + btoa(username + ':' + password)
            },
            params: {
                'response_type': 'code',
                'client_id': 'sampleClientId'
            }
        };

        return this.http
            .get(this.authorizeURL, optionsGet)
            .catch((error: HttpErrorResponse) => {
                // OAuth returns 401 Unauthorized with a Location header containing a code
                if (error.status !== 401) {
                    return throwError('Login failed: error code ' + error.message);
                }

                // Parse the Location header to get the code
                if (error.url.indexOf('code=') === -1) {
                    // Wrong credentials
                    return throwError('Login failed: wrong credentials');
                }
                const code = error.url.substring(error.url.indexOf('code=') + 5);
                // Prepare for the second step
                const headersPost = {
                    'Authorization': 'Basic ' + btoa('sampleClientId:secret'),
                    'Content-Type': 'application/x-www-form-urlencoded'
                };
                const bodyPost = 'code=' + code + '&grant_type=authorization_code&client_id=sampleClientId';

                // Send request: OAuth responds with a data structure containing the access token
                return this.http.post(this.tokenURL, bodyPost, {headers: headersPost});
            })
            .map((result) => {
                // Isolate the access token
                const token = result['access_token'];

                // Save the token
                this.saveToken(token);
                this.updateBalance();
                return empty();
            })
            .catch((error) => {
                // An error occurred, notify the user
                if (error) {
                    return throwError(error);
                } else {
                    return throwError('Login failed');
                }
                
            });
    }

    // Performs logout and redirects to the homepage
    public logOut(): Observable<any> {
        return this.http.get(this.logoutURL)
            .map(result => this.deleteToken());
    }

    // Checks if a username already exists
    public checkUsername(user: string): Observable<boolean> {
        const optionsGet = {
            headers: {},
            params: {'user': user}
        };
        return this.http.get<boolean>(this.checkExistenceURL, optionsGet);
    }

    // Performs user registration
    public register(user: string, pass: string): Observable<any> {
        const bodyPost = {
            username: user,
            password: pass
        };
        return this.http.post(this.registerURL, bodyPost)
            .catch((err) => {
                // Otherwise notify the user
                return throwError('Registration failed: server error')
            });
    }

    // Returns the raw JWT token string
    public getRawToken(): string {
        return this.storageService.get(this.tokenStorageKey);
    }

    // Returns the JWT token as an object
    private getParsedToken() {
        if (this.getRawToken() === undefined) {
            return;
        }
        return this.jwtHelper.decodeToken(this.getRawToken());
    }

    private saveToken(token: string): void {
        this.storageService.save(this.tokenStorageKey, token);
    }

    private deleteToken(): void {
        this.storageService.delete(this.tokenStorageKey);
    }

    // Returns the username of the currently logged user
    public getLoggedUsername(): string {
        return this.getParsedToken().user_name;
    }

    // Returns the balance of the currently logged user
    public getBalance(): number {
        return this.currentBalance;
    }

    // Updates the balance of the currently logged user
    public updateBalance(): void {
        if (this.isLogged()) {
            this.http.get(this.profileURL)
                .map(result => result['balance'])
                .subscribe(data => this.currentBalance = data);
        }
    }
}
