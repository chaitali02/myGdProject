import { BrowserModule } from '@angular/platform-browser';
import { DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { routing } from './app.routes';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TooltipModule } from 'primeng/primeng';
import { UiSwitchModule } from 'ngx-ui-switch';
import { NgBootstrapFormValidationModule } from 'ng-bootstrap-form-validation';


import { ProjectSharedModule } from './shared/module/shared.module';

import { LoginComponent } from './login/login.component';

import { SharedService } from './shared/shared.service';
import { LoginService } from './login/login.service';

import { AppConfig } from './app.config';
import { AppMetadata } from './app.metadata';
import { AppHelper } from './app.helper';

const APP_PROVIDERS = [AppConfig, AppMetadata, AppHelper];

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
    TooltipModule,
    NgBootstrapFormValidationModule.forRoot(),

    UiSwitchModule.forRoot({
      size: 'small',
      color: 'rgb(0, 189, 99)',
      switchColor: '#80FFA2',
      defaultBgColor: '#00ACFF',
      defaultBoColor : '#476EFF',
      checkedLabel: 'on',
      uncheckedLabel: 'off'
    })
  ],

  providers: [
    APP_PROVIDERS,
    DatePipe,
    LoginService,
    SharedService
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
