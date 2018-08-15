// Interface that models a User in user list of the purchase view

export interface UserListItem {
    // Username
    user: string;

    // Number of archives of the user among all the archives found
    archives: number;

    // User archives are included in the purchase
    selected: boolean;

    // User color to highlight positions on the map and in the time chart
    color: string;
}