import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewCreditDocumentsComponent } from './view-credit-documents.component';

describe('ViewCreditDocumentsComponent', () => {
  let component: ViewCreditDocumentsComponent;
  let fixture: ComponentFixture<ViewCreditDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewCreditDocumentsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewCreditDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
