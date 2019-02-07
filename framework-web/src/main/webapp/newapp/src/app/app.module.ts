import { BrowserModule } from '@angular/platform-browser';
import { DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { routing } from './app.routes';
// import { AgGridModule } from 'ag-grid-angular/main';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import * as c3 from 'c3'

import { ProjectSharedModule } from './shared/module/shared.module';

import { LoginComponent } from './login/login.component';

import { SharedService } from './shared/shared.service';
import { LoginService } from './login/login.service';

import { AppConfig } from './app.config';
import { AppMetadata } from './app.metadata';
import { AppHepler } from './app.helper';
import { TooltipModule } from 'primeng/primeng';
import { CookieService } from 'ngx-cookie-service';
const APP_PROVIDERS = [AppConfig, AppMetadata, AppHepler];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent
  ],
  // entryComponents: [DropdownComponent],
  imports: [
    routing,
    BrowserModule,
    FormsModule,
    HttpModule,
    BrowserAnimationsModule,
    ProjectSharedModule.forRoot(),
    TooltipModule
  ],

  providers: [
    APP_PROVIDERS,
    DatePipe,
    LoginService,
    SharedService,
    CookieService
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
