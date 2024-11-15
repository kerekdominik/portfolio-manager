import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioValueBarchartComponent } from './portfolio-value-barchart.component';

describe('PortfolioValueBarchartComponent', () => {
  let component: PortfolioValueBarchartComponent;
  let fixture: ComponentFixture<PortfolioValueBarchartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PortfolioValueBarchartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PortfolioValueBarchartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
