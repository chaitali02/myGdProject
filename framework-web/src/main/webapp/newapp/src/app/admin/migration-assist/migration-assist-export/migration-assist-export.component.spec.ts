import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MigrationAssistExportComponent } from './migration-assist-export.component';

describe('MigrationAssistExportComponent', () => {
  let component: MigrationAssistExportComponent;
  let fixture: ComponentFixture<MigrationAssistExportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MigrationAssistExportComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MigrationAssistExportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
