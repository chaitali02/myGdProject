import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProjectSharedModule } from '../../shared/module/shared.module';
import {jobMonitoringDetailsComponent} from './jobMonitoringDetails.component'
import {LoadExecComponent} from './loadExec/loadExec.component';
import {MapExecComponent} from './mapExec/mapExec.component';
import {VizExecComponent} from './vizExec/vizExec.component';
import { ModelExecComponent } from './modelExec/modelExec.component';
import { ProfileExecComponent } from './profileExec/profileExec.component';
import { ProfileGroupExecComponent } from './profileGroupExec/profileGroupExec.component';
import { DqExecComponent } from './dqExec/dqExec.component';
import { DqGroupExecComponent } from './dqGroupExec/dqGroupExec.component';
import { RuleExecComponent } from './ruleExec/ruleExec.component';
import { RuleGroupExecComponent } from './ruleGroupExec/ruleGroupExec.component';
import { PipelineExecComponent } from './pipelineExec/pipelineExec.component';

const routes: Routes = [
    {
         path: '', component: jobMonitoringDetailsComponent,
         children: [
            {path: 'loadExec/:id/:version/:mode', component: LoadExecComponent,pathMatch:'full' },
            {path: 'mapExec/:id/:version/:mode', component: MapExecComponent,pathMatch:'full' },
            {path: 'vizExec/:id/:version/:mode', component: VizExecComponent,pathMatch:'full' },
            {path: 'modelExec/:id/:version/:mode', component: ModelExecComponent,pathMatch:'full' },
            {path: 'profileExec/:id/:version/:mode', component: ProfileExecComponent,pathMatch:'full' },
            {path: 'profilegroupExec/:id/:version/:mode', component: ProfileGroupExecComponent,pathMatch:'full' },
            {path: 'dqExec/:id/:version/:mode', component: DqExecComponent,pathMatch:'full' },
            {path: 'dqgroupExec/:id/:version/:mode', component: DqGroupExecComponent,pathMatch:'full' },
            {path: 'ruleExec/:id/:version/:mode', component: RuleExecComponent,pathMatch:'full' },
            {path: 'rulegroupExec/:id/:version/:mode', component: RuleGroupExecComponent,pathMatch:'full' },
            {path: 'dagExec/:id/:version/:mode', component: PipelineExecComponent,pathMatch:'full' }            
         ]
    }
]

export const routing: ModuleWithProviders = RouterModule.forChild(routes)