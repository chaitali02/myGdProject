import { Routes, RouterModule } from "@angular/router";
import { DataIngestionComponent } from "./data-ingestion.component";
import { ModuleWithProviders } from "@angular/core";
import { DataIngestionDetailComponent } from "./data-ingestion-detail/data-ingestion-detail.component";
import { DataIngestionRuleGroupComponent } from "./data-ingestion-rule-group/data-ingestion-rule-group.component";

const routes: Routes = [
    {
        path: '',
        component: DataIngestionComponent,
        children: [
            { path: 'ingest/:id/:version/:mode', component: DataIngestionDetailComponent},
            { path: 'ingestgroup/:id/:version/:mode', component: DataIngestionRuleGroupComponent},
            { path: 'ingest', component: DataIngestionDetailComponent},
            { path: 'ingestgroup', component: DataIngestionRuleGroupComponent},
        ]
    }
]

export const routing1: ModuleWithProviders = RouterModule.forChild(routes);