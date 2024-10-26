import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {LoginGuard} from './core/guards/login.guard';
import {RegisterComponent} from './components/register/register.component';
import {Oauth2RedirectComponent} from './components/oauth2-redirect/oauth2-redirect.component';
import {AuthGuard} from './core/guards/auth.guard';
import {PortfolioComponent} from './components/portfolio/portfolio.component';
import {ProfileComponent} from './components/profile/profile.component';

export const routes: Routes = [
  { path: '', component: LoginComponent, canActivate: [LoginGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [LoginGuard] },
  { path: 'oauth2/redirect', component: Oauth2RedirectComponent },
  {
    path: 'dashboard',
    loadChildren: () => import('./components/dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard]
  },
  { path: 'portfolio', component: PortfolioComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
