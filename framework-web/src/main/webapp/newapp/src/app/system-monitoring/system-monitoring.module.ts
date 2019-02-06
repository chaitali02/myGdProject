
import { NgModule } from '@angular/core';
import { CommonModule , DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { MessagesModule} from 'primeng/primeng';
import { MessageModule} from 'primeng/primeng';
import {DropdownModule,SelectItem} from 'primeng/primeng';
import {DataTableModule,SharedModule} from 'primeng/primeng';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { SystemMonitoringComponent} from './system-monitoring.component';
import {GrowlModule} from 'primeng/primeng';
// import { DatePickerModule } from 'angular-io-datepicker';
// import { OverlayModule } from 'angular-io-overlay';
import { BsDropdownModule } from 'ngx-bootstrap';
import {TabViewModule} from 'primeng/primeng';
import { routing1 } from './system-monitoring-routing.module';
import {MenuModule,MenuItem} from 'primeng/primeng';
import {ButtonModule} from 'primeng/primeng';
import {CalendarModule} from 'primeng/primeng';
import { SharedService } from '../shared/shared.service';
import { SystemMonitoringService } from '../metadata/services/systemMonitoring.service';
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        // UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        //NgSelectModule,
        DropdownModule,
        DataTableModule,
        SharedModule,
        GrowlModule ,
        TagInputModule,
      //  DatePickerModule,
      //  OverlayModule,
        BsDropdownModule,
        TabViewModule,
        MenuModule,
        ButtonModule,
        CalendarModule,

        
    ],
    declarations: [
        SystemMonitoringComponent,
        
    ],
    providers: [
        SystemMonitoringService,
        SharedService
    ]

})
export class SystemMonitoringModule { }