import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Archive} from '../models/archive';
import {Polygon} from 'leaflet';
import {ApproxArchive} from '../models/approxarchive';

@Injectable({
    providedIn: 'root'
})

// This service handles archive-related requests
export class ArchiveService {

    // Approximate archives URL: used to retrieve approximate archives when a search is performed
    private approxArchiveURL: string = '/api/archives';

    // Uploaded archives URL: used to retrieve and upload owned archives
    private uploadedArchiveURL: string = '/api/archives/uploaded';

    // Bought archives URL: used to retrieve bought archives
    private boughtArchiveURL: string = '/api/archives/bought';

    constructor(
        private http: HttpClient
    ) {
    }

    getUserOwnArchives(): Observable<Archive[]> {
        const apiUrl = this.uploadedArchiveURL;
        return this.http.get<Archive[]>(apiUrl);
    }

    // id: id of the archive to be deleted
    deleteOwnArchive(id: string): Observable<Object> {
        const apiUrl = this.uploadedArchiveURL + '/' + id;
        return this.http.delete(apiUrl);
    }

    getUserBoughtArchives(): Observable<Archive[]> {
        const apiUrl = this.boughtArchiveURL;
        return this.http.get<Archive[]>(apiUrl);
    }

    // id: id of the bought archive to retrieve
    getBoughtArchive(id: string): Observable<Archive> {
        const apiUrl = this.boughtArchiveURL + '/' + id;
        return this.http.get<Archive>(apiUrl);
    }

    // area: a GeoJSON compliant polygon
    // from: the beginning of the timespan of the search
    // to: the end of the timespan of the search
    getApproxArchives(area: Polygon, from: Date, to: Date): Observable<ApproxArchive[]> {

        // Convert dates to epoch timestamp
        const fromMillis = from.getTime();
        const toMillis = to.getTime();

        // Parse the polygon
        const coordinates = area.toGeoJSON().geometry.coordinates;
        const areaString = JSON.stringify({
            'type': 'Polygon',
            'coordinates': coordinates
        });

        // Prepare the request
        let getParams = new HttpParams();
        getParams = getParams.append('from', fromMillis.toString());
        getParams = getParams.append('to', toMillis.toString());
        getParams = getParams.append('area', areaString);

        return this.http.get<ApproxArchive[]>(this.approxArchiveURL, {params: getParams});
    }
}
