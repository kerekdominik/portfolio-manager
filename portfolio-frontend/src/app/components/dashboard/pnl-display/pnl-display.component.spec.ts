import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PnlDisplayComponent } from './pnl-display.component';

describe('PnlDisplayComponent', () => {
  let component: PnlDisplayComponent;
  let fixture: ComponentFixture<PnlDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PnlDisplayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PnlDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
