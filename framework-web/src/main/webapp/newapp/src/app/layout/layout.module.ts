import { CommonService } from './../metadata/services/common.service';

import { NgModule } from '@angular/core';
import { CommonModule , DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular/main';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
// import { DatePickerModule } from 'angular-io-datepicker';
// import { OverlayModule } from 'angular-io-overlay';
import { BsDropdownModule } from 'ngx-bootstrap';
import {GrowlModule} from 'primeng/primeng';
import { PaginationModule } from 'ngx-bootstrap';
// import {SelectModule} from 'ng-select';
import {DataTableModule,SharedModule} from 'primeng/primeng';
import {MenuModule,MenuItem} from 'primeng/primeng';
import {ButtonModule} from 'primeng/primeng';
import {DropdownModule,SelectItem} from 'primeng/primeng';
// import { OrderModule } from 'ngx-order-pipe';

import { ProjectSharedModule } from '../shared/module/shared.module';

import { LayoutComponent } from './layout.component';
import { HeaderComponent, SidebarComponent,FooterComponent} from '../shared/components';
import { DataDiscoveryComponent} from '../data-discovery/datadiscovery.component';
import { CommonListComponent } from '../common-list/common-list.component';
import { CommonListService } from '../common-list/common-list.service';

import { ModelComponent} from '../shared/popUpModel/model.component';
import { metadataNavigatorComponent} from '../metadata-navigator/metadataNavigator.component';
import { jobMonitoringComponent} from '../job-monitoring/jobMonitoring.component';

import { LayoutService} from './layout.service';
import { SharedService } from '../shared/shared.service';
import { HeaderService } from '../shared/components/header/header.service';
import { SidebarService } from '../shared/components/sidebar/sidebar.service';
import { DatadiscoveryService} from '../data-discovery/datadiscovery.service';
import { metadataNavigatorService} from '../metadata-navigator/metadataNavigator.service';
import { jobMonitoringService} from '../job-monitoring/jobMonitoring.service'; 

import { MessagesModule} from 'primeng/primeng';
import { MessageModule} from 'primeng/primeng';
import { routing1 } from './layout-routing.module';

import {FilterPipeDD} from '../data-discovery/pipes/search-pipe';
import {FilterJobPipe} from '../job-monitoring/pipes/search-pipe';
import {FilterMetaPipe} from '../metadata-navigator/pipes/search-pipe';
import {OrderBy} from '../data-discovery/pipes/orderBy';
import {OrderByJob} from '../job-monitoring/pipes/orderBy';
import {OrderByMeta} from '../metadata-navigator/pipes/orderBy';
import { CalendarModule } from 'primeng/components/calendar/calendar';
// import { SystemMonitoringService } from '../metadata/services/systemMonitoring.service';
// import { SystemMonitoringComponent } from '../system-monitoring/system-monitoring.component'
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        AgGridModule.withComponents(''),
        UiSwitchModule,
        TagInputModule,
        routing1,
        // DatePickerModule,
        // OverlayModule,
        BsDropdownModule.forRoot(),
        MessagesModule,
        MessageModule,
        ProjectSharedModule,
        PaginationModule.forRoot(),
        DataTableModule,
        SharedModule,
        MenuModule,
        ButtonModule,
        GrowlModule,
        DropdownModule,
        CalendarModule
                
    ],
    declarations: [
        LayoutComponent,
        HeaderComponent,
        SidebarComponent,
        FooterComponent,
        DataDiscoveryComponent,
        CommonListComponent,
        ModelComponent,
        metadataNavigatorComponent,
        jobMonitoringComponent,
        FilterPipeDD,
        FilterJobPipe,
        OrderBy,
        OrderByJob,
        OrderByMeta,
        FilterMetaPipe
    ],
    providers: [
        LayoutService,
        SharedService,
        HeaderService,
        SidebarService,
        DatadiscoveryService,
        CommonListService,
        metadataNavigatorService,
        jobMonitoringService,
        CommonService
    ],
    entryComponents: [],
})
export class LayoutModule { }
