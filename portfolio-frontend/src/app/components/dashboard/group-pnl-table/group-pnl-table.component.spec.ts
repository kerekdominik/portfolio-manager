import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupPnlTableComponent } from './group-pnl-table.component';

describe('GroupPnlTableComponent', () => {
  let component: GroupPnlTableComponent;
  let fixture: ComponentFixture<GroupPnlTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupPnlTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupPnlTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
