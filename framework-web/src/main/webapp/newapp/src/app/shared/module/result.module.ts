import { NgModule, Input } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { DataTableModule, SharedModule,MultiSelectModule,DropdownModule, SelectItem } from 'primeng/primeng';
import { MessagesModule } from 'primeng/primeng';
import { GrowlModule } from 'primeng/primeng';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { TableRenderComponent } from '../components/resulttable/resulttable.component'
import { JointjsGroupComponent } from '../components/jointjsgroup/jointjsgroup.component'
import { jointjsGroupService } from '../components/jointjsgroup/joinjsgroup.service'
import { JointjsComponent } from '../components/jointjs/jointjs.component';
import { JointjsService } from '../components/jointjs/jointjsservice';
import { ShContextMenuModule } from 'ng2-right-click-menu';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';

@NgModule({
    imports: [CommonModule, RouterModule, DataTableModule, SharedModule, MultiSelectModule, DropdownModule, GrowlModule,
        ShContextMenuModule,FormsModule,ReactiveFormsModule
    ],
    exports: [CommonModule, GrowlModule, DataTableModule, SharedModule, MultiSelectModule, DropdownModule, TableRenderComponent, JointjsGroupComponent,
        JointjsComponent
    ],
    declarations: [TableRenderComponent, JointjsGroupComponent,
        JointjsComponent
    ],
    providers: [jointjsGroupService,
        JointjsService
    ]
})
export class ResultModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: ResultModule,
            //providers: [D3Service,jointjsGroupService]
        };
    }
}