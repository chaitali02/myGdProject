import { SessionComponent } from './session/session.component';
import { UserComponent } from './user/user.component';
import { RoleComponent } from './role/role.component';
import { PrivilegeComponent } from './privilege/privilege.component';
import { GroupComponent } from './group/group.component';
import { DatastoreComponent } from './datastore/datastore.component';
import { ModuleWithProviders } from '@angular/core';
import { AdminComponent } from './admin.component';
import { Routes, RouterModule } from '@angular/router';
import { ActivityComponent } from './activity/activity.component';
import { ApplicationComponent } from './application/application.component';
import { DatasourceComponent } from './datasource/datasource.component';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { RegisterSourceComponent } from './registerSource/registerSource.component';
import { SettingsComponent } from './settings/settings.component';
import { MigrationAssistComponent } from './migration-assist/migration-assist.component';
import { MigrationAssistExportComponent } from './migration-assist/migration-assist-export/migration-assist-export.component';
import { MigrationAssistImportComponent } from './migration-assist/migration-assist-import/migration-assist-import.component';

const routes: Routes = [
    {
         path: '', component: AdminComponent,
         children: [
            //{path: '', component: DatapodComponent},
            {path: 'activity/:id/:version/:mode', component: ActivityComponent,pathMatch:'full' },
            {path: 'activity/activity/:id/:version/:mode', component:ActivityComponent, pathMatch: 'full'},  
            {path: 'application/:id/:version/:mode', component: ApplicationComponent,pathMatch:'full' },
            {path: 'application/application/:id/:version/:mode', component:ApplicationComponent, pathMatch: 'full'}, 
            {path: 'application', component: ApplicationComponent, pathMatch: 'full'},
            {path: 'datasource/:id/:version/:mode', component: DatasourceComponent,pathMatch:'full' },
            {path: 'datasource/datasource/:id/:version/:mode', component:DatasourceComponent, pathMatch: 'full'}, 
            {path: 'datasource', component: DatasourceComponent, pathMatch: 'full'}, 
            {path: 'datastore/:id/:version/:mode', component: DatastoreComponent,pathMatch:'full' },
            {path: 'datastore/datastore/:id/:version/:mode', component:DatastoreComponent, pathMatch: 'full'},  
            {path: 'datastore', component: DatastoreComponent, pathMatch: 'full'}, 
            {path: 'group/:id/:version/:mode', component: GroupComponent,pathMatch:'full' },
            {path: 'group/group/:id/:version/:mode', component:GroupComponent, pathMatch: 'full'}, 
            {path: 'group', component: GroupComponent, pathMatch: 'full'},             
            {path: 'privilege/:id/:version/:mode', component: PrivilegeComponent,pathMatch:'full' },
            {path: 'privilege/privilege/:id/:version/:mode', component:PrivilegeComponent, pathMatch: 'full'}, 
            {path: 'privilege', component: PrivilegeComponent, pathMatch: 'full'},  
            {path: 'role/:id/:version/:mode', component: RoleComponent,pathMatch:'full' },
            {path: 'role/role/:id/:version/:mode', component:RoleComponent, pathMatch: 'full'},
            {path: 'role', component: RoleComponent, pathMatch: 'full'},  
            {path: 'session/:id/:version/:mode', component: SessionComponent,pathMatch:'full' },
            {path: 'session/session/:id/:version/:mode', component:SessionComponent, pathMatch: 'full'}, 
            {path: 'session', component: SessionComponent, pathMatch: 'full'}, 
            {path: 'user/:id/:version/:mode', component: UserComponent,pathMatch:'full' },
            {path: 'user/user/:id/:version/:mode', component:UserComponent, pathMatch: 'full'},  
            {path: 'user', component: UserComponent, pathMatch: 'full'},
            {path: 'registerSource/:id/:version/:mode', component: RegisterSourceComponent,pathMatch:'full' },
            {path: 'registerSource/registerSource/:id/:version/:mode', component:RegisterSourceComponent, pathMatch: 'full'},  
            {path: 'registerSource', component: RegisterSourceComponent, pathMatch: 'full'},
            {path: 'settings/:id/:version/:mode', component: SettingsComponent,pathMatch:'full' },
            {path: 'settings/settings/:id/:version/:mode', component:SettingsComponent, pathMatch: 'full'},  
            {path: 'settings', component: SettingsComponent, pathMatch: 'full'},
            
            {path: 'migration-assist/type/:id/:version/:mode', component: MigrationAssistComponent,pathMatch:'full' },
            {path: 'migration-assist/migration-assist/:id/:version/:mode', component:MigrationAssistComponent, pathMatch: 'full'},  
            {path: 'migration-assist', component: MigrationAssistComponent, pathMatch: 'full'},
            
            {path: 'migration-assist/export/:id/:version/:mode', component: MigrationAssistExportComponent,pathMatch:'full' },
            {path: 'migration-assist/migration-assist/export/:id/:version/:mode', component:MigrationAssistExportComponent, pathMatch: 'full'},  
            {path: 'migration-assist/export', component: MigrationAssistExportComponent, pathMatch: 'full'},
            
            {path: 'migration-assist/import/:id/:version/:mode', component: MigrationAssistImportComponent,pathMatch:'full' },
            {path: 'migration-assist/migration-assist/import/:id/:version/:mode', component:MigrationAssistImportComponent, pathMatch: 'full'},  
            {path: 'migration-assist/import', component: MigrationAssistImportComponent, pathMatch: 'full'},
            
        ]
    }
]
export const routing1: ModuleWithProviders = RouterModule.forChild(routes);
