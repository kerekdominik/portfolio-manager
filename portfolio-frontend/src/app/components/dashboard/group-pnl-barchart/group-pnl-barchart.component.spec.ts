import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupPnlBarchartComponent } from './group-pnl-barchart.component';

describe('GroupPnlBarchartComponent', () => {
  let component: GroupPnlBarchartComponent;
  let fixture: ComponentFixture<GroupPnlBarchartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupPnlBarchartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupPnlBarchartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
