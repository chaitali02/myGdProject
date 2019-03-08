import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataIngestionComponent } from './data-ingestion.component';
import { routing1 } from './data-ingestion-routing.module';
import { DataIngestionDetailComponent } from './data-ingestion-detail/data-ingestion-detail.component'
import { FormsModule } from '@angular/forms';
import { GrowlModule, ChipsModule } from 'primeng/primeng';
import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { CheckboxModule } from 'primeng/components/checkbox/checkbox';
import { TagInputModule } from 'ngx-chips';
import { DataIngestionService } from './../metadata/services/dataIngestion.service';
import { DialogModule } from 'primeng/components/dialog/dialog';
import { DataIngestionRuleGroupComponent } from './data-ingestion-rule-group/data-ingestion-rule-group.component';
import { DataIngestionResultsComponent } from './data-ingestion-results/data-ingestion-results.component';
import { UiSwitchModule } from 'ngx-ui-switch';
@NgModule({
  imports: [
    CommonModule,
    routing1,
    FormsModule,
    TagInputModule,
    CheckboxModule,
    routing1,
    GrowlModule,
    ResultModule,
    ProjectSharedModule,
    DialogModule,
    CheckboxModule,
    AngularMultiSelectModule,
    ChipsModule,
    UiSwitchModule.forRoot({
      size: 'small',
      color: '#15C5D5',
      switchColor: '#FFFFF',
      defaultBoColor : '#ccc',
     // checkedLabel: 'Yes',
      //uncheckedLabel: 'No'
    })
  ],
  declarations: [
    DataIngestionComponent,
    DataIngestionDetailComponent,
    DataIngestionRuleGroupComponent,
    DataIngestionResultsComponent,
  ],
  providers: [
    DataIngestionService
  ]
})

export class DataIngestionModule { }
