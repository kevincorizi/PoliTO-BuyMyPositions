import {Component, OnInit, Input} from '@angular/core';
import {ArchiveListItem} from '../../models/approxarchive';
import {UserListItem} from '../../models/user';

@Component({
    selector: 'app-time-chart',
    templateUrl: './time-chart.component.html',
    styleUrls: ['./time-chart.component.scss']
})

// This component implements the time chart diagram in the purchase view
// It is based on the Google Chart API, specifically on the Scatter Chart
// Refer to https://developers.google.com/chart/interactive/docs/gallery/scatterchart
export class TimeChartComponent implements OnInit {

    // The archives to plot
    private _archives: ArchiveListItem[];

    // The users to plot
    private _users: UserListItem[];

    @Input()
    set archives(archives: ArchiveListItem[]) {
        this._archives = archives;
    }

    @Input()
    set users(users: UserListItem[]) {
        this._users = users;
    }

    // Chart header data
    private scatter_ChartData_Header = [
        // X Axis shows the dates
        {type: 'date', id: 'Date', label: 'Date'},
        // Y Axis shows one row for each user
        {type: 'number', id: 'User', label: 'User'},
        // Custom column used to style the plotted points
        {type: 'string', role: 'style'},
        // Custom column used to define a custom tooltip when the user hovers a point
        {type: 'string', role: 'tooltip', 'p': {'html': true}}
    ];

    // Chart data structure
    public scatter_ChartData = {
        // Chart type
        chartType: 'ScatterChart',
        // Chart data (initially empty)
        dataTable: [],
        options: {
            // Allow HTML custom tooltips
            allowHtml: true,
            // Customize the Y axis
            vAxis: {
                // No title
                title: '',
                // Set color to white to blend with the background and make it invisible
                textStyle: {
                    color: 'white'
                },
                // Set the initial tick to 0 (only integer values)
                ticks: [0]
            },
            // Define the tooltip type
            tooltip: {
                isHtml: true
            },
            // Hide the legend
            legend: {
                position: 'none'
            },
            // Set the chart to span the full width of the container element
            chartArea: {
                left: 0,
                width: '100%'
            }
        },
        formatters: [
            // Define a format for the date
            {
                columns: [0],
                type: 'DateFormat',
                options: {
                    pattern: "MMM d, yyyy H:m"
                }
            },
            // Allow only integer values on the Y axis
            {
                columns: [1],
                type: 'NumberFormat',
                options: {
                    pattern: '#'
                }
            }
        ]
    }

    constructor() {
    }

    ngOnInit() {
        this.refresh();
    }

    refresh(): void {
        // Set the header row
        this.scatter_ChartData.dataTable = [this.scatter_ChartData_Header];

        // Assign a row index to each user, ignoring duplicates
        let usersIndex = [];
        this._users.forEach(u => {
            if (u.selected) {
                if (usersIndex.indexOf(u.user) == -1) {
                    usersIndex.push(u.user);
                    this.scatter_ChartData.options.vAxis.ticks.push(usersIndex.length);
                }
            }
        });
        this.scatter_ChartData.options.vAxis.ticks.push(usersIndex.length + 1);

        this._archives.forEach(a => {
            if (a.selected) {
                // This archive has to be considered among the plotted data
                const user = usersIndex.indexOf(a.archive.ownerUsername) + 1;
                const color = a.color;
                a.archive.approxTimestampArchive.timestamps.forEach(t => {
                    // Set the custom style (color of the point)
                    const fill = 'point {fill-color: ' + color + ';}';
                    // Convert timestamp to date object
                    const date = new Date(t);
                    // Add the point to the diagram along with its custom tooltip
                    this.scatter_ChartData.dataTable.push([date, user, fill, this.createTooltip(date, a.archive.ownerUsername)]);
                });
            }
        });
    }

    // Creates a custom HTML tooltip for a point in the chart
    // The tooltip displays the owner and the date corresponding to the point
    // date: the date to be displayed in the tooltip
    // user: the user to be displayed in the tooltip
    createTooltip(date: Date, user: string) {
        const dateString = date.toLocaleDateString();
        const timeString = date.toLocaleTimeString();
        const tooltip =
            '<div style="padding: 10px">' +
            '<p><strong>' + user + '</strong></p>' +
            '<p>' + dateString + ' ' + timeString + '</p>' +
            '</div>';
        return tooltip;
    }
}