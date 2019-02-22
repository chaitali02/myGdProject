
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataQualityComponent } from './data-quality.component';
import { DataQualityResultComponent } from './data-qualityresult.component'
import { DataQualityDetailComponent } from './data-qualitydetail.component'
import { DataQualityGroupDetailComponent } from './data-qualitygroupdetail.components'
// import { CompareResultComponent } from './compareresult/compareresult.component';

const routes: Routes = [{
  path: '',
  component: DataQualityComponent,
  children: [
    { path: ':type/:id/:version/:type/:mode', component: DataQualityResultComponent },
    { path: 'dq/:id/:version/:mode', component: DataQualityDetailComponent },
    { path: 'dq', component: DataQualityDetailComponent },
    { path: 'dqgroup/:id/:version/:mode', component: DataQualityGroupDetailComponent },
    { path: 'dqgroup', component: DataQualityGroupDetailComponent },
    //{ path: 'compare', component: CompareResultComponent }
  ]
}
];

export const routing1: ModuleWithProviders = RouterModule.forChild(routes);