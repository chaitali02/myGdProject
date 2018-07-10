
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataVisualizationComponent } from './data-visualization.component';
import { DashboardComponent} from './dashboard/dashboard.component'
import { DashboardDetailComponent} from './dashboard/dashboarddetail.component';
import { VizpodDetailComponent } from './vizpod/vizpoddetail.component'

const routes: Routes = [{ 
	  path: '',
    component: DataVisualizationComponent,
    children: [
      { path: 'dashboard/:id/:version/:mode', component: DashboardComponent },
      { path: 'dashboarddetail/:id/:version/:mode', component: DashboardDetailComponent },
      { path: 'dashboarddetail', component: DashboardDetailComponent },
      { path: 'vizpod/:id/:version/:mode', component: VizpodDetailComponent },
      { path: 'vizpod', component: VizpodDetailComponent }
    ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);