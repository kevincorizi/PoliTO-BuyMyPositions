import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {LandingComponent} from './components/landing/landing.component';
import {LoginComponent} from './components/login/login.component';
import {UserComponent} from './components/user/user.component';
import {AuthGuard} from './guards/auth.guard';
import {UserGuard} from './guards/user.guard';
import {NoAuthGuard} from './guards/no-auth.guard';
import {SignupComponent} from './components/signup/signup.component';
import {UserPositionsComponent} from './components/user-positions/user-positions.component';
import {UserUploadComponent} from './components/user-upload/user-upload.component';
import {UserPurchasesComponent} from './components/user-purchases/user-purchases.component';
import {UserPurchaseComponent} from './components/user-purchase/user-purchase.component';

const appRoutes: Routes = [
    {
        path: '',
        component: LandingComponent // Default Route
    },
    {
        path: 'login',
        component: LoginComponent,
        canActivate: [NoAuthGuard]
    },
    {
        path: 'signup',
        component: SignupComponent,
        canActivate: [NoAuthGuard]
    },
    {
        path: 'user',
        component: UserComponent,
        canActivate: [AuthGuard, UserGuard],
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'view'
            },
            {
                path: 'view',
                component: UserPositionsComponent
            },
            {
                path: 'upload',
                component: UserUploadComponent
            },
            {
                path: 'purchases',
                component: UserPurchasesComponent
            },
            {
                path: 'purchase',
                component: UserPurchaseComponent
            }
        ]
    },
    {
        path: '**',
        component: LandingComponent,
        redirectTo: ''
    }
];

@NgModule({
    declarations: [],
    imports: [RouterModule.forRoot(appRoutes)],
    providers: [],
    bootstrap: [],
    exports: [RouterModule]
})

export class AppRoutingModule {
}
