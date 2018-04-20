import { Settings } from './../metadata/domain/domain.settings';
import { AdminComponent } from './admin.component';
import { TagInputModule } from 'ngx-chips';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { AgGridModule } from 'ag-grid-angular/main';
import { FormsModule } from '@angular/forms';
import { CommonModule , DatePipe} from '@angular/common';
import { NgModule } from '@angular/core';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { MessagesModule, MessageModule, MenuItem, TabMenuModule, TabViewModule, GrowlModule} from 'primeng/primeng';
import { SelectModule } from 'ng-select';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';

import { MetaDataDataPodService } from '../data-preparation/datapod/datapod.service';
import { ActivityComponent } from './activity/activity.component';
import { routing1 } from './admin-routing.module';
import { CommonService } from '../metadata/services/common.service';
import { ApplicationComponent } from './application/application.component';
import { DatasourceComponent } from './datasource/datasource.component';
import { DatastoreComponent } from './datastore/datastore.component';
import { GroupComponent } from './group/group.component';
import { PrivilegeComponent } from './privilege/privilege.component';
import { RoleComponent } from './role/role.component';
import { SessionComponent } from './session/session.component';
import { UserComponent } from './user/user.component';
import { RegisterSourceComponent } from './registerSource/registerSource.component';
import { DatapodService } from '../metadata/services/datapod.service';
import { SettingsComponent } from './settings/settings.component';
import { SettingsService } from '../metadata/services/settings.service';
import { RegisterSourceService } from '../metadata/services/registerSource.service';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { MigrationAssistComponent } from './migration-assist/migration-assist.component'
import { MigrationAssistExportComponent } from './migration-assist/migration-assist-export/migration-assist-export.component'
import { CommonListService } from '../common-list/common-list.service';
import { MenuModule } from 'primeng/components/menu/menu';
import { DataTableModule } from 'primeng/components/datatable/datatable';
import { CalendarModule } from 'primeng/components/calendar/calendar';

@NgModule({
    imports:[
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        AgGridModule,
        UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        SelectModule,
        AngularMultiSelectModule,
        TabViewModule,  
        GrowlModule,  
        DropdownModule ,
        CalendarModule,
        DataTableModule,
        MenuModule
    ],
    
    declarations :[
        AdminComponent,
        ActivityComponent,
        ApplicationComponent,
        DatasourceComponent,
        DatastoreComponent,
        GroupComponent,
        PrivilegeComponent,
        RoleComponent,
        SessionComponent,
        UserComponent,
        RegisterSourceComponent,
        SettingsComponent,
        MigrationAssistComponent,
        MigrationAssistExportComponent
        
    ],
    providers:[
        MetaDataDataPodService,
        CommonService,
        DatapodService,
        SettingsService,
        RegisterSourceService,
        CommonListService
    ]
})
 
export class AdminModule{}