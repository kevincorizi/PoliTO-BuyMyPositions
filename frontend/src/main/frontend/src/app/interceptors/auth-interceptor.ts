import {AuthenticationService} from '../services/authentication.service';
import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';

@Injectable()

// This interceptor appends authentication information to any authenticated request
export class AuthInterceptor implements HttpInterceptor {
    constructor(private auth: AuthenticationService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler) {
        const authToken = this.auth.getRawToken();

        // If the user is not authenticated, leave the request unchanged
        if (!authToken) {
            return next.handle(req);
        }

        // Otherwise, set the authorization header
        return next.handle(req.clone({
            headers: req.headers.set('Authorization', 'Bearer ' + authToken)
        }));
    }
}
