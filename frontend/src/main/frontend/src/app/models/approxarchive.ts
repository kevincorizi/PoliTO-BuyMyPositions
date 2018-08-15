import {Position} from './position';

// Interface that models the positions part of an approximate archive
interface ApproxPositionArchive {
    geoPoints: Array<Position>;
}

// Interface that models the timestamps part of an approximate archive
interface ApproxTimestampArchive {
    timestamps: Array<number>;
}

// Class that implements an approximate archive
export class ApproxArchive {
    // Id of the real archive approximated by this archive
    realArchiveId: string;

    // Number of positions included in the original archive
    // This number may be different than the number of positions
    // in the approximate archive due to the duplicate removal
    realPositions: number;

    // Username of the archive owner
    ownerUsername: string;

    // Position part of the approximate archive
    approxPositionArchive: ApproxPositionArchive;

    // Timestamp part of the approximate archive
    approxTimestampArchive: ApproxTimestampArchive;
}

// Interface that implements an Archive in the archive list in the purchase page
export interface ArchiveListItem {
    // Approximate archive
    archive: ApproxArchive;

    // Archive included in the purchase
    selected: boolean;

    // Archive color to highlight positions in the map and the time chart
    color: string;
}