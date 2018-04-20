import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParamsetComponent } from './paramset.component';

describe('ParamsetComponent', () => {
  let component: ParamsetComponent;
  let fixture: ComponentFixture<ParamsetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParamsetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParamsetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
