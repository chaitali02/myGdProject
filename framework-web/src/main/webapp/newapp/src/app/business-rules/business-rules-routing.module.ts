
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BusinessRulesComponent } from './business-rule.component';
import { BusinessRulesDetailComponent } from './business-rulesdetail.component';
import { BusinessRulesGroupDetailComponent } from './business-rulesgroupdetail.components';
import { BusinessRulesResultComponent } from './business-rulesresult.component';
import { ParamlistComponent } from '../shared/components/paramlist/paramlist.component';

const routes: Routes = [{ 
	  path: '',
    component: BusinessRulesComponent,
    children: [
      { path: 'paramlist/:parentType/:id/:version/:mode', component: ParamlistComponent}, 
      { path: 'paramlist/:parentType', component: ParamlistComponent} , 

      { path: ':type/:id/:version/:type/:mode', component: BusinessRulesResultComponent},
      { path: 'rule/:id/:version/:mode', component: BusinessRulesDetailComponent},    
      { path: 'rule', component: BusinessRulesDetailComponent},   
      { path: 'rulegroup/:id/:version/:mode', component: BusinessRulesGroupDetailComponent},    
      { path: 'rulegroup', component: BusinessRulesGroupDetailComponent}
  
    ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);