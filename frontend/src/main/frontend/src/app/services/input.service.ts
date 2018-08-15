import {Injectable, EventEmitter} from '@angular/core';
import {Polygon} from 'leaflet';
import {Archive} from '../models/archive';
import {ArchiveListItem} from '../models/approxarchive';

@Injectable({
    providedIn: 'root'
})

// This service implements cross-component signaling and data exchange
export class InputService {

    constructor() {
    }

    // Event used to notify changes in the display area of the map (pan, zoom, explicit polygon)
    public polygonSetUnsetEvents: EventEmitter<Polygon> = new EventEmitter();

    // Event used to push obtained archives to the map (that will show them)
    public archiveEvent: EventEmitter<Archive[]> = new EventEmitter();

    // Event used to push obtained approximate position to the map (that will show them)
    public approxArchiveEvent: EventEmitter<ArchiveListItem[]> = new EventEmitter();

    public polygonSet(polygon: Polygon) {
        this.polygonSetUnsetEvents.emit(polygon);
    }

    public polygonUnset() {
        this.polygonSetUnsetEvents.emit(null);
    }

    public pushArchives(archives: Archive[]) {
        this.archiveEvent.emit(archives);
    }

    public pushApproxArchives(archives: ArchiveListItem[]) {
        this.approxArchiveEvent.emit(archives);
    }
}
