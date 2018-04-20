import { NgModule,Input } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { CommonModule }        from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import {MultiSelectModule} from 'primeng/primeng';
import {DropdownModule,SelectItem} from 'primeng/primeng';
import {DataTableModule,SharedModule} from 'primeng/primeng';
import { MessagesModule} from 'primeng/primeng';
import {GrowlModule} from 'primeng/primeng';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import {TableRenderComponent} from '../components/resulttable/resulttable.component'
import {JointjsGroupComponent} from '../components/jointjsgroup/jointjsgroup.component'
import {jointjsGroupService} from '../components/jointjsgroup/joinjsgroup.service'
@NgModule({
    imports: [CommonModule,RouterModule,DataTableModule,SharedModule,MultiSelectModule,DropdownModule,GrowlModule],
    exports: [CommonModule,GrowlModule,DataTableModule,SharedModule,MultiSelectModule,DropdownModule,TableRenderComponent,JointjsGroupComponent],
    declarations: [TableRenderComponent,JointjsGroupComponent],
    providers:[jointjsGroupService]
})
export class ResultModule {
    static forRoot() : ModuleWithProviders {
        return {
            ngModule: ResultModule,
            //providers: [D3Service,jointjsGroupService]
        };
    }
 }