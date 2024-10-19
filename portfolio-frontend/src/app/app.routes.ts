import { Routes } from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {RegisterComponent} from './components/register/register.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {Oauth2RedirectComponent} from './components/oauth2-redirect/oauth2-redirect.component';
import {AuthGuard} from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'oauth2/redirect', component: Oauth2RedirectComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
];
