import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyCryptoPricesComponent } from './daily-crypto-prices.component';

describe('DailyPricesComponent', () => {
  let component: DailyCryptoPricesComponent;
  let fixture: ComponentFixture<DailyCryptoPricesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DailyCryptoPricesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DailyCryptoPricesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
