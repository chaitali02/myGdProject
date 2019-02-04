import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppMetadata } from '../app.metadata';

@Component({
  selector: 'app-data-ingest',
  templateUrl: './data-ingestion.template.html',
  styleUrls: []
})
export class DataIngestionComponent {
  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata) {
  }
}