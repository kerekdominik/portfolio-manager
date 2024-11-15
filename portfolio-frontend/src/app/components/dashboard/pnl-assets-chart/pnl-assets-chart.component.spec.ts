import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PnlAssetsChartComponent } from './pnl-assets-chart.component';

describe('PnlAssetsChartComponent', () => {
  let component: PnlAssetsChartComponent;
  let fixture: ComponentFixture<PnlAssetsChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PnlAssetsChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PnlAssetsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
