
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ProjectSharedModule } from '../shared/module/shared.module';

import { SystemMonitoringComponent} from './system-monitoring.component';


const routes: Routes = [
    {
        path: '', component: SystemMonitoringComponent,
    }
]
export const routing1: ModuleWithProviders = RouterModule.forChild(routes);