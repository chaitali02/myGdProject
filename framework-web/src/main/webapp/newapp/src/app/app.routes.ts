import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {LoginComponent} from './login/login.component';


const appRoutes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent, data: { name: 'impactLogin', pathMatch: 'full' }},
  { path: 'app', loadChildren: 'app/layout/layout.module#LayoutModule'},
  { path: '**', redirectTo: 'pageNotFound' } // otherwise redirect to home
];

export const routing : ModuleWithProviders = RouterModule.forRoot(appRoutes);
