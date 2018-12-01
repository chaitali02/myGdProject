import { DataScienceComponent } from './../data-science/data-science.component';
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LayoutComponent } from './layout.component';
import {DataDiscoveryComponent} from '../data-discovery/datadiscovery.component'
import {CommonListComponent} from '../common-list/common-list.component';
import {D3Component} from '../D3/d3.component';
import { metadataNavigatorComponent} from '../metadata-navigator/metadataNavigator.component';
import { jobMonitoringComponent} from '../job-monitoring/jobMonitoring.component';
import { RegisterSourceComponent } from '../admin/registerSource/registerSource.component';



const routes: Routes = [
    {
        path: '', component: LayoutComponent,
            children: [
                { path: 'DataDiscovery', component: DataDiscoveryComponent},
                { path: 'DataDiscovery/dataPreparation', redirectTo: 'dataPreparation'},
                
                { path: 'list/:type', component: CommonListComponent, data: { name: 'impactLogin' }},
                { path: 'list/:parentType/:type', component: CommonListComponent, data: { name: 'impactLogin' }},
                
                { path: 'dataVisualization', loadChildren: 'app/data-visualization/data-visualization.module#DataVisualizationModule'},
                { path: 'list/:type/dataVisualization',redirectTo :'dataVisualization'},
                
                { path: 'dataPreparation', loadChildren: 'app/data-preparation/data-preparation.module#DataPreparationModule'},
                { path: 'list/:type/dataPreparation',redirectTo :'dataPreparation'},
                { path: 'list/formula/formula/:id/:version/:mode', redirectTo : 'formula/:id/:version/:mode' },
                
                { path: 'metadataNavigator', component: metadataNavigatorComponent},
                { path: 'metadataNavigator/list/:type',redirectTo :'list/:type'},
                
                { path: 'systemMonitoring', loadChildren: 'app/system-monitoring/system-monitoring.module#SystemMonitoringModule'},
                //{ path: 'systemMonitoring/list/:type',redirectTo :'list/:type'},

                
                { path: 'jobMonitoring', component: jobMonitoringComponent},
                { path: 'jobMonitoring/list/:type',redirectTo :'list/:type'},
                { path: 'list/:type/JobMonitoring',redirectTo :'JobMonitoring'},
                { path: 'JobMonitoring', loadChildren: 'app/job-monitoring/job-monitoring-details/job-monitoring-details.module#jobMonitoringDetailsModule'},
            
                { path: 'admin', loadChildren: 'app/admin/admin.module#AdminModule'},
                { path: 'list/:type/admin',redirectTo :'admin'},
            //    { path: 'registerSource', component: RegisterSourceComponent},

                { path: 'dataScience', loadChildren: 'app/data-science/data-science.module#DataScienceModule'},
                { path: 'list/:type/dataScience',redirectTo :'dataScience'},
                            
                { path: 'dataPipeline', loadChildren: 'app/data-pipeline/data-pipeline.module#DataPiplineModule'},
                { path: 'list/:type/dataPipeline',redirectTo :'dataPipeline'},
                { path: 'dataPipeline/:type/:id/:version/:mode/dataPreparation',redirectTo :'dataPreparation'},
                { path: 'dataPipeline/:type/:id/:version/:type/:mode/dataQuality',redirectTo :'dataQuality'},
                { path: 'dataPipeline/:type/:id/:version/:type/:mode/dataProfiling',redirectTo :'dataProfiling'},
                
                { path: 'dataProfiling', loadChildren: 'app/data-profile/data-profile.module#DataProfileModule'},
               // { path: 'dataProfiling/profilegroupexec/:type/:id/:version/:mode/dataProfiling',redirectTo :'dataProfiling'},
                { path: 'list/:type/dataProfiling',redirectTo :'dataProfiling'},
                
                { path: 'businessRules', loadChildren: 'app/business-rules/business-rules.module#BusinessRulesModule'},
                { path: 'businessRules/rulegroupexec/:type/:id/:version/:mode/businessRules',redirectTo :'businessRules'},
                { path: 'list/:type/businessRules',redirectTo :'businessRules'},

                { path: 'dataQuality', loadChildren: 'app/data-quality/data-quality.module#DataQualityModule'},
                { path: 'list/:type/dataQuality',redirectTo :'dataQuality'},
                { path: 'dataQuality/dqgroupexec/:type/:id/:version/:mode/dataQuality',redirectTo :'dataQuality'},

                { path: 'recon', loadChildren: 'app/data-recon/data-recon.module#DataReconModule'},
                { path: 'list/:type/recon',redirectTo :'recon'},
                { path: 'recongroup', loadChildren: 'app/data-recon/data-recon.module#DataReconModule'},
                { path: 'list/:type/recongroup',redirectTo :'recongroup'},
                { path: 'list/:type/reconexec',redirectTo :'reconexec'},
                { path: 'reconexec', loadChildren: 'app/data-recon/data-recon.module#DataReconModule'},
                { path: 'graphAnalysis', loadChildren: 'app/graph-analysis/graph-analysis.module#graphAnalysisModule'},
              // { path: 'reconexec/viewdrresultspage/:type/:id/:version/:mode/reconexec',redirectTo :'reconexec'},
  
            ]
       },
];

export const routing1: ModuleWithProviders = RouterModule.forChild(routes);

