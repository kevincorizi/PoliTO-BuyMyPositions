import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarControlsComponent } from './navbar-controls.component';

describe('NavbarControlsComponent', () => {
  let component: NavbarControlsComponent;
  let fixture: ComponentFixture<NavbarControlsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NavbarControlsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarControlsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
