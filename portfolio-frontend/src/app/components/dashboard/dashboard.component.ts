import { Component } from '@angular/core';
import {MatGridList, MatGridTile} from '@angular/material/grid-list';
import {MatCard} from '@angular/material/card';
import {DailyCryptoPricesComponent} from './daily-crypto-prices/daily-crypto-prices.component';
import {PnlDisplayComponent} from './pnl-display/pnl-display.component';
import {PortfolioCompositionComponent} from './portfolio-composition/portfolio-composition.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    DailyCryptoPricesComponent,
    PnlDisplayComponent,
    PortfolioCompositionComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
