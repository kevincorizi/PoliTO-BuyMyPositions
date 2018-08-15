// Class that implements a position included in an archive
export class Position {
    // Timestamp of the position
    timestamp: number;

    // Coordinates
    geoPoint: GeoPoint;
}

class GeoPoint {
    x: number;
    y: number;
    coordinates: [number, number];
    type: string;
}