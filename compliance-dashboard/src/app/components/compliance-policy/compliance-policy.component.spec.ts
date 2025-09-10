import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompliancePolicyComponent } from './compliance-policy.component';

describe('CompliancePolicyComponent', () => {
  let component: CompliancePolicyComponent;
  let fixture: ComponentFixture<CompliancePolicyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompliancePolicyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompliancePolicyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
