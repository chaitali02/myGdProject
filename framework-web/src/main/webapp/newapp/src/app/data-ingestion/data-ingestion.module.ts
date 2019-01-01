import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataIngestionComponent } from './data-ingestion.component';
import { routing1 } from './data-ingestion-routing.module';
import { DataIngestionDetailComponent } from './data-ingestion-detail/data-ingestion-detail.component'
import { FormsModule } from '@angular/forms';
import { GrowlModule } from 'primeng/primeng';
import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { CheckboxModule } from 'primeng/components/checkbox/checkbox';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/multiselect.component';
import { DataIngestionService } from './../metadata/services/dataIngestion.service';
import { DialogModule } from 'primeng/components/dialog/dialog';
import { DataIngestionRuleGroupComponent } from './data-ingestion-rule-group/data-ingestion-rule-group.component';
@NgModule({
  imports: [
    CommonModule,
    routing1,
    FormsModule,
    UiSwitchModule,
    TagInputModule,
    AngularMultiSelectModule,
    CheckboxModule,
    routing1,
    GrowlModule,
    ResultModule,
    ProjectSharedModule,
    DialogModule,
    CheckboxModule,
    AngularMultiSelectModule,
  ],
  declarations: [
    DataIngestionComponent,
    DataIngestionDetailComponent,
    DataIngestionRuleGroupComponent,
  ],
  providers: [
    DataIngestionService
  ]
})

export class DataIngestionModule { }
