import { MetadataService } from './../metadata/services/metadata.service';
import { Settings } from './../metadata/domain/domain.settings';
import { AdminComponent } from './admin.component';
import { TagInputModule } from 'ngx-chips';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { FormsModule } from '@angular/forms';
import { CommonModule , DatePipe} from '@angular/common';
import { NgModule } from '@angular/core';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { MessagesModule, MessageModule, TabViewModule, GrowlModule, ChipsModule} from 'primeng/primeng';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';

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
import { ApplicationService } from '../metadata/services/application.service';
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
import { MigrationAssistImportComponent } from './migration-assist/migration-assist-import/migration-assist-import.component';
import { MigrationAssistService } from '../metadata/services/migration-assist.services';
import { SettingsDetailsComponent } from './settings/settings-details/settings-details.component';
import { TabMenuModule } from 'primeng/components/tabmenu/tabmenu';
import { MenuItem } from 'primeng/components/common/api';
import { FileManagerComponent } from './file-manager/file-manager.component';
import { FileManagerService } from '../metadata/services/fileManager.service';
import { DialogModule } from 'primeng/components/dialog/dialog';
import { UiSwitchModule } from 'ngx-ui-switch';

@NgModule({
    imports:[
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        AngularMultiSelectModule,
        TabViewModule,  
        GrowlModule,  
        DropdownModule ,
        CalendarModule,
        DataTableModule,
        MenuModule,
        TabMenuModule,
        DialogModule,
        ChipsModule,
        UiSwitchModule.forRoot({
            size: 'small',
            color: '#15C5D5',
            switchColor: '#FFFFF',
            defaultBoColor: '#ccc',
            // checkedLabel: 'Yes',
            //uncheckedLabel: 'No'
        })
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
        MigrationAssistExportComponent,
        MigrationAssistImportComponent,
        SettingsDetailsComponent,
        FileManagerComponent
    ],
    providers:[
        MetaDataDataPodService,
        CommonService,
        DatapodService,
        SettingsService,
        RegisterSourceService,
        CommonListService,
        MigrationAssistService,
        ApplicationService,
        FileManagerService,
        MetadataService
    ]
})
 
export class AdminModule{}