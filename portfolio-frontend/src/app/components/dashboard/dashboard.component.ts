import { Component } from '@angular/core';
import {DailyCryptoPricesComponent} from './daily-crypto-prices/daily-crypto-prices.component';
import {PnlDisplayComponent} from './pnl-display/pnl-display.component';
import {PortfolioCompositionComponent} from './portfolio-composition/portfolio-composition.component';
import {MatCard, MatCardTitle} from '@angular/material/card';
import {PnlAssetsChartComponent} from './pnl-assets-chart/pnl-assets-chart.component';
import {GroupPnlBarChartComponent} from './group-pnl-barchart/group-pnl-barchart.component';
import {PortfolioValueBarchartComponent} from './portfolio-value-barchart/portfolio-value-barchart.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    DailyCryptoPricesComponent,
    PnlDisplayComponent,
    PortfolioCompositionComponent,
    MatCard,
    PnlAssetsChartComponent,
    GroupPnlBarChartComponent,
    PortfolioValueBarchartComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
