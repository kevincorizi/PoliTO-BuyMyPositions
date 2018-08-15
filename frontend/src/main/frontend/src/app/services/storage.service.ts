import {Injectable} from '@angular/core';
import {CookiesStorageService} from 'ngx-store';

@Injectable({
    providedIn: 'root'
})

// This service handles cookie management so that the data source can be changed without affecting the other services
export class StorageService {

    constructor(
        // Cookies are persistent and won't be cleared on page reload
        private cookiesStorageService: CookiesStorageService
    ) {
    }

    public save(key: string, object: Object) {
        this.cookiesStorageService.set(key, object);
    }

    public delete(key: string): void {
        this.cookiesStorageService.remove(key);
    }

    public get(key: string): any {
        return this.cookiesStorageService.get(key);
    }

}
