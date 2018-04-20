import { NgModule,Input } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { CommonModule }       from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { C3Component } from '../components';
import{ClockComponent,BreadcrumbComponent} from '../components';
import { D3Component } from '../../D3/d3.component';

@NgModule({
    imports: [CommonModule,RouterModule],
    exports: [CommonModule,ClockComponent,BreadcrumbComponent,D3Component,C3Component],
    declarations: [ClockComponent,BreadcrumbComponent,D3Component,C3Component],
})
export class ProjectSharedModule {
    static forRoot() : ModuleWithProviders {
        return {
            ngModule: ProjectSharedModule,
            providers: [D3Service]
        };
    }
 }