import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParamlistComponent } from './paramlist.component';

describe('ParamlistComponent', () => {
  let component: ParamlistComponent;
  let fixture: ComponentFixture<ParamlistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParamlistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParamlistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
