import {Component, Input, NgZone, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {
    icon,
    latLng,
    marker,
    polygon,
    tileLayer,
    Map as LeafletMap,
    LeafletMouseEvent,
    Layer,
    Polygon,
    DomUtil,
    DivIcon,
    DivIconOptions
} from 'leaflet';
import {InputService} from '../../services/input.service';
import {Subscription} from 'rxjs';
import {Coordinates} from '../../models/coordinates';
import {Archive} from '../../models/archive';
import {ArchiveListItem} from '../../models/approxarchive';
import hasClass = DomUtil.hasClass;

@Component({
    selector: 'app-map',
    templateUrl: './map.component.html',
    styleUrls: ['./map.component.css'],
    encapsulation: ViewEncapsulation.None
})

// This component implements the map view included in the owned archives and purchase view
export class MapComponent implements OnInit, OnDestroy {

    // Enables (purchase) or disables (owned positions) map click events
    @Input()
    clickAllowed: boolean;

    // Shows (purchase) or hides (owned positions) the flush button to reset the map
    @Input()
    showFlush: boolean;

    // Enables (purchase) or disables (owned positions) zoom and pan notifications from the map
    @Input()
    trackZoom: boolean;

    // Stores all the markers on the map
    private markerCoordinates: Coordinates[] = [];

    // Stores all the positions currently displayed on the map
    private currentPositions: any[] = [];

    // True when a polygon is explicitly drawn on the map (it is not affected by zoom and pan events)
    private polygonSet = false;

    // Map object
    private map: LeafletMap;

    // Subscriptions to the input service event emitters
    private archiveProviderSubscription: Subscription;
    private approxArchiveProviderSubscription: Subscription;

    // Limit the outgoing zoom on the map
    private worldBounds = [[-90, 180], [90, -180]];

    // Tracks the first marker set by the user to allow for easy precision-independent polygon closing
    private firstMarker = undefined;

    // Leaflet map options
    options = {
        layers: [
            tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 18,
                attribution: '...',
                noWrap: true
            })
        ],
        zoom: 4,
        center: latLng(41.89282173182968, 12.474546432495115),
        maxBounds: this.worldBounds,
        maxBoundsViscosity: 1.0
    };
    layers: Layer[] = [];

    constructor(
        private inputService: InputService,
        private zone: NgZone
    ) {
    }

    // Subscribe to the input service event emitters
    ngOnInit() {
        this.archiveProviderSubscription = this.inputService.archiveEvent.subscribe(archives => {
            if (archives != null) {
                this.showArchives(archives);
            }
        });
        this.approxArchiveProviderSubscription = this.inputService.approxArchiveEvent.subscribe(archives => {
            if (archives != null) {
                this.showApproxArchives(archives);
            }
        });
    }

    // Subscribe from the input service event emitters
    ngOnDestroy(): void {
        this.archiveProviderSubscription.unsubscribe();
        this.approxArchiveProviderSubscription.unsubscribe();
    }

    // Registers the event handlers for the map (click, zoom and pan)
    // Called when the map object is fully initialized
    // map: the Leaflet map object become ready
    onMapReady(map: LeafletMap) {
        this.map = map;

        // Register click event handler
        map.on('click', (position: LeafletMouseEvent) => {
            if (this.clickAllowed) {
                this.mapClicked(position.latlng.lat, position.latlng.lng, <HTMLElement>position.originalEvent.target);
            }
        });

        // Force update of the zoom bounds for the map
        this.zone.run(() => {
            this.map.options.minZoom = this.map.getBoundsZoom(this.map.options.maxBounds) + 2;
        })

        // Register zoom and pan event handler
        map.on('moveend', () => {
            this.updateBounds();
        });

        // Notify the current view area using the input service
        this.updateBounds();
    }

    // Update the current view area and notify the input service
    updateBounds() {
        if (this.trackZoom) {
            // The zoom and pan tracking is enabled
            if (!this.polygonSet) {
                // If there is no explicitly drawn polygon on the map (it cannot be overwritten)
                const bounds = this.map.getBounds();
                this.inputService.polygonSet(new Polygon([
                    bounds.getSouthWest(),
                    bounds.getSouthEast(),
                    bounds.getNorthEast(),
                    bounds.getNorthWest(),
                    bounds.getSouthWest()
                ]));
            }
        }
    }

    // Map click event handler
    // lat: latitude
    // lng: longitude
    // target: the HTML element corresponding to the clicked point on the map
    mapClicked(lat: number, lng: number, target: HTMLElement) {
        if (Math.abs(lat) > 90 || Math.abs(lng) > 180) {
            // Block invalid clicks
            return;
        }
        if (this.polygonSet) {
            // Avoid multipolygon
            return;
        }
        if (hasClass(target, 'flush-map') || hasClass(target, 'fa-close')) {
            // Block clicks on the flush button
            return;
        }
        if (hasClass(target, 'leaflet-marker-icon')) {
            // Block click on markers (except the first one)
            return;
        }

        // Store the new marker's coordinates
        this.markerCoordinates.push({latitude: lat, longitude: lng});

        // Add marker where the user clicked
        this.addMarker(lat, lng);
    }

    // Displays a marker on the map where the user clicked
    // lat: latitude
    // lng: longitude
    addMarker(lat: number, lng: number) {
        // You need to run this in zone because otherwise angular won't detect changes. It is a choice of the ngx-leaflet developers.
        // https://github.com/Asymmetrik/ngx-leaflet#a-note-about-change-detection
        this.zone.run(() => {
            // Get the marker icon
            const iconUrl = '../../assets/marker-icon.png';

            // Create the Leaflet marker object
            const newMarker = marker(
                [lat, lng],
                {icon: icon({iconSize: [32, 32], iconAnchor: [16, 32], iconUrl: iconUrl})}
            );

            // Display the marker on the map
            this.layers.push(newMarker);

            if (this.firstMarker == undefined) {
                // This is the first marker, register a custom click event on it to close the polygon
                newMarker.on('click', (e) => {
                    this.closePolygon();
                });
                this.firstMarker = newMarker;
            }
        });
    }


    closePolygon() {
        // Polygon closed, get the first marker's coordinates
        // In a GeoJSON polygon the first and last coordinates must be the same
        const lat = this.markerCoordinates[0].latitude;
        const lng = this.markerCoordinates[0].longitude;

        // Draw the polygon on the map and emit the event
        this.markerCoordinates.push({latitude: lat, longitude: lng});
        this.polygonSet = true;
        const polygon: Polygon = this.fromCoordinatesToPolygon();
        this.addPolygon(polygon);
        this.inputService.polygonSet(polygon);
    }

    // Shows on the map all the positions of the real archives received by the input service
    // from the owned archives view of the user page
    // archives: the list of complete archives to be displayed
    showArchives(archives: Archive[]) {
        // Empty the map
        this.layers = [];
        this.currentPositions = [];
        this.addPolygon(this.fromCoordinatesToPolygon());

        archives.forEach(
            archive => {
                archive.positions.forEach(
                    // For each position, add a spot on the map
                    // The color is set to blue because all the positions belong to the same user
                    position => this.addPosition(position.geoPoint.y, position.geoPoint.x, 'blue')
                )
            }
        );

        // Adapt the map display area to fit the current positions
        this.centerAndZoom();
    }

    // Shows on the map all the positions of the approximate archives received by the
    // input service from the purchase view of the user page
    // archives: the list of approximate archives to be displayed
    showApproxArchives(archives: ArchiveListItem[]) {
        // Empty the map
        this.layers = [];
        this.currentPositions = [];
        this.addPolygon(this.fromCoordinatesToPolygon());

        archives.forEach(
            a => {
                a.archive.approxPositionArchive.geoPoints.forEach(
                    // For each position, add a spot on the map
                    // The color is included in the archive to guarantee that each user has a different color
                    position => this.addPosition(position.geoPoint.y, position.geoPoint.x, a.color)
                )
            }
        );

        // Adapt the map display area to fit the current positions
        this.centerAndZoom();
    }

    // Displays a position on the map
    // lat: latitude
    // lng: longitude
    // color: user-unique marker color
    addPosition(lat: number, lng: number, color: string) {
        this.zone.run(() => {
            this.layers.push(
                marker(
                    [lat, lng],
                    {icon: this.generateUserMarker(color)}
                )
            );
            this.currentPositions.push([lat, lng]);
        });
    }

    // Displays an explicitly drawn polygon on the map
    // polygon: the polygon to be displayed
    addPolygon(polygon: Polygon) {
        // You need to run this in zone because otherwise angular won't detect changes. It is a choice of the ngx-leaflet developers.
        // https://github.com/Asymmetrik/ngx-leaflet#a-note-about-change-detection
        this.zone.run(() => {
            this.layers.push(polygon);
        });
    }

    // Converts the local coordinates to a polygon
    fromCoordinatesToPolygon(): Polygon {
        const latlngArray = [];
        this.markerCoordinates.forEach((ele) => {
            latlngArray.push([ele.latitude, ele.longitude]);
        });
        return polygon([latlngArray]);
    }

    // Resets the map to its initial state
    flushMap() {
        // Delete coordinates of the search area (polygon)
        this.markerCoordinates = [];

        // Clean map from markers and polygons
        this.layers = [];

        this.polygonSet = false;
        this.firstMarker = undefined;

        // Inform the other components that the polygon has been unset
        this.updateBounds();
    }

    // Generates the custom marker used to display the position of a user on the map
    // color: a user-unique color
    generateUserMarker(color: string): DivIcon {
        const userMarkerStyle = `
      background-color: ${color};
      width: 14px;
      height: 14px;
      display: block;
      left: -7px;
      top: -7px;
      position: relative;
      border-radius: 50%;
      border: 2px solid #FFFFFF`;

        let options: DivIconOptions = {
            html: `<span style="${userMarkerStyle}" />`,
            iconAnchor: [10, 10],
            className: "position-marker"
        };
        let icon: DivIcon = new DivIcon(options);
        return icon;
    }

    // Adjusts the map view area to fit all the current positions displayed
    centerAndZoom() {
        if (this.currentPositions.length > 0) {
            this.map.fitBounds(this.currentPositions);
        }
    }
}
