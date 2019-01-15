
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataReconComponent } from './data-recon.component';
import{DataReconresultComponent} from './data-reconresult.component'
 import{DataReconDetailComponent} from './data-recondetail.component'
import{DataReconGroupDetailComponent} from './data-reconGroupdetail.component'
const routes: Routes = [{ 
	  path: '',
    component: DataReconComponent,
    children: [
      //{ path: ':type/:id/:version/:type/:mode', component: DataQualityResultComponent},
      { path: 'viewdrresultspage/:id/:version/:type/:mode', component: DataReconresultComponent},
      { path: 'viewdrresultspage/:id/:version/:type/:mode', component: DataReconresultComponent},
      { path: 'createreconerule/:id/:version/:mode', component: DataReconDetailComponent},    
      { path: 'createreconerule', component: DataReconDetailComponent}, 
      { path: 'createreconerulegroup/:id/:version/:mode', component: DataReconGroupDetailComponent},    
      { path: 'createreconerulegroup', component: DataReconGroupDetailComponent},  
      { path: 'viewdrresultspage/:id/:version/:type/:mode/recon/createreconerule/:id/:version/:mode', redirectTo: 'createreconerule/:id/:version/:mode'},
      //{ path: 'dqgroup/:id/:version/:mode', component: DataQualityGroupDetailComponent},    
      //{ path: 'dqgroup', component: DataQualityGroupDetailComponent},     
            
  ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);
