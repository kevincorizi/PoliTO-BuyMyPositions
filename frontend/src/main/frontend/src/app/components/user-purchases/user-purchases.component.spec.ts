import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPurchasesComponent } from './user-purchases.component';

describe('UserPurchasesComponent', () => {
  let component: UserPurchasesComponent;
  let fixture: ComponentFixture<UserPurchasesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserPurchasesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserPurchasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
