import {Position} from './position';

// Class that implements a non-approximate archive
export class Archive {
    // Archive id
    id: string;

    // Username of the owner of the archive
    ownerUsername: string;

    // The archive is still available for purchase
    canBeBought: boolean;

    // Original upload date
    uploadTime: Date;

    // Number of times the archive has been bought as part of a transaction
    timesBought: number;

    // Positions included in the archive
    positions: Array<Position>;
}