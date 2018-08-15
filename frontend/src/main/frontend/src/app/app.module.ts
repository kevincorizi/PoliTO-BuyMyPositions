import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {LeafletModule} from '@asymmetrik/ngx-leaflet';
import {RouterModule} from '@angular/router';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {WebStorageModule} from 'ngx-store';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {MapComponent} from './components/map/map.component';
import {LoginComponent} from './components/login/login.component';
import {UserComponent} from './components/user/user.component';
import {LandingComponent} from './components/landing/landing.component';
import {AuthInterceptor} from './interceptors/auth-interceptor';
import {FileUploadModule} from 'ng2-file-upload';
import {SignupComponent} from './components/signup/signup.component';
import {UserPositionsComponent} from './components/user-positions/user-positions.component';
import {UserUploadComponent} from './components/user-upload/user-upload.component';
import {UserPurchasesComponent} from './components/user-purchases/user-purchases.component';
import {UserPurchaseComponent} from './components/user-purchase/user-purchase.component';
import {
    MatExpansionModule,
    MatProgressBarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatListModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatMenuModule,
    MatIconModule,
    MatCardModule
} from '@angular/material';
import {
    OwlDateTimeModule,
    OwlNativeDateTimeModule
} from 'ng-pick-datetime';
import {DialogComponent} from './components/dialog/dialog.component';
import {PurchaseDialogComponent} from './components/purchase-dialog/purchase-dialog.component';
import {NavbarControlsComponent} from './components/navbar-controls/navbar-controls.component';
import {FlexLayoutModule} from '@angular/flex-layout';
import {TimeChartComponent} from './components/time-chart/time-chart.component';
import {Ng2GoogleChartsModule} from 'ng2-google-charts';

@NgModule({
    declarations: [
        AppComponent,
        MapComponent,
        LoginComponent,
        UserComponent,
        LandingComponent,
        SignupComponent,
        UserPositionsComponent,
        UserUploadComponent,
        UserPurchasesComponent,
        UserPurchaseComponent,
        DialogComponent,
        PurchaseDialogComponent,
        NavbarControlsComponent,
        TimeChartComponent,
    ],
    imports: [
        BrowserModule,
        LeafletModule.forRoot(),
        FormsModule,
        AppRoutingModule,
        RouterModule,
        HttpClientModule,
        WebStorageModule,
        FileUploadModule,
        FlexLayoutModule,
        BrowserAnimationsModule,
        MatExpansionModule,
        MatProgressBarModule,
        MatDatepickerModule,
        MatNativeDateModule,
        OwlDateTimeModule,
        OwlNativeDateTimeModule,
        MatFormFieldModule,
        MatInputModule,
        MatCheckboxModule,
        MatListModule,
        MatButtonModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatToolbarModule,
        MatMenuModule,
        MatIconModule,
        MatCardModule,
        Ng2GoogleChartsModule
    ],
    schemas: [NO_ERRORS_SCHEMA],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        MatDatepickerModule
    ],
    bootstrap: [AppComponent],
    entryComponents: [DialogComponent, PurchaseDialogComponent]
})
export class AppModule {
}
