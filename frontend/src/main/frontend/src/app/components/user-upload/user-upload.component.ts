import {Component, OnInit} from '@angular/core';
import {FileUploader, FileUploaderOptions, FileItem, ParsedResponseHeaders} from 'ng2-file-upload';
import {AuthenticationService} from '../../services/authentication.service';
import {MatSnackBar} from '@angular/material';
import {JsonConvert, OperationMode, ValueCheckingMode} from 'json2typescript';
import {UploadPosition} from '../../models/uploadposition';

@Component({
    selector: 'app-user-upload',
    templateUrl: './user-upload.component.html',
    styleUrls: ['./user-upload.component.scss']
})

// This component implements the upload view of the user page
export class UserUploadComponent implements OnInit {

    // Variables and functions related to the file uploader
    // Position posting is completely handled by the file uploader
    public hasBaseDropZoneOver = false;
    private options: FileUploaderOptions;
    public uploader: FileUploader;

    constructor(
        private authService: AuthenticationService,
        private snackBar: MatSnackBar
    ) {
    }

    ngOnInit() {
        // Configure the uploader
        this.options = {
            method: 'POST',
            url: '/api/archives/uploaded',
            isHTML5: true,
            disableMultipart: true,
            authToken: 'Bearer ' + this.authService.getRawToken()
        };
        // Initialize the uploader
        this.uploader = new FileUploader(this.options);

        // Register a custom error handler upon failed upload to
        // display the error message on the bottom of the page
        this.uploader.onErrorItem = ((item: FileItem, response: string, status: number, headers: ParsedResponseHeaders) => {
            this.snackBar.open('An error occurred uploading file ' + item.file.name + ': ' + response, '', {duration: 5000});
        });

        // Register a custom listener on the file uploader to trigger
        // when a user adds a new file to the upload queue
        this.uploader.onAfterAddingFile = this.afterAddingFile;
    }

    // Function used by file uploader to determine whether the user is about to drop a file in the drop zone
    // e: the drop event
    public fileOverBase(e: any): void {
        this.hasBaseDropZoneOver = e;
    }

    // Handles a new file in the upload queue
    // file: the file added to the upload queue
    public afterAddingFile(file: FileItem) {
        // Read the file and try to parse it into a propert Archive object
        // If parsing fails, the content is invalid and the upload is blocked
        let fileReader = new FileReader();
        fileReader.onload = (e) => {
            let jsonConvert: JsonConvert = new JsonConvert(
                OperationMode.DISABLE,
                ValueCheckingMode.DISALLOW_NULL,
                false
            );
            try {
                let jsonObject = JSON.parse(fileReader.result);
                let archive: UploadPosition[] = jsonConvert.deserialize(jsonObject, UploadPosition);
            } catch (ex) {
                // Override isCancel to signal invalid files (there is no dedicated field in the FileUploader)
                file.isError = true;
                file.isCancel = true;
            }
        }
        fileReader.readAsText(file._file);
    }

    // Enables or disables the Upload button of the FileUploader
    canUpload(): boolean {
        let result = true;
        this.uploader.getNotUploadedItems().forEach((file: FileItem) => {
            if (file.isCancel || file.isError) {
                result = false;
            }
        });
        return result;
    }

}
