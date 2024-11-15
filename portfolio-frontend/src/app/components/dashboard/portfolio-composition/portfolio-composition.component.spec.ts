import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioCompositionComponent } from './portfolio-composition.component';

describe('PortfolioCompositionComponent', () => {
  let component: PortfolioCompositionComponent;
  let fixture: ComponentFixture<PortfolioCompositionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PortfolioCompositionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PortfolioCompositionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
