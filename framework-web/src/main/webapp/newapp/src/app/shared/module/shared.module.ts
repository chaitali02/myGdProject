import { NgModule,Input } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { CommonModule }       from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { C3Component } from '../components';
import { ClockComponent,BreadcrumbComponent } from '../components';
import { D3Component } from '../../D3/d3.component';
import { ModelComponent} from '../popUpModel/model.component';
import { ParamlistComponent } from '../components/paramlist/paramlist.component';
import { FormsModule } from '@angular/forms';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { ParamlistService } from '../../metadata/services/paramlist.service';

@NgModule({
    imports: [CommonModule,RouterModule,FormsModule, UiSwitchModule,TagInputModule,DropdownModule],
    exports: [CommonModule,ClockComponent,BreadcrumbComponent,D3Component,C3Component,ModelComponent,ParamlistComponent],
    declarations: [ClockComponent,BreadcrumbComponent,D3Component,C3Component,ModelComponent,ParamlistComponent],
    providers:[ParamlistService]
})
export class ProjectSharedModule {
    static forRoot() : ModuleWithProviders {
        return {
            ngModule: ProjectSharedModule,
            providers: [D3Service]
        };
    }
 }