import { Component } from '@angular/core';
import {MatGridList, MatGridTile} from '@angular/material/grid-list';
import {DailyCryptoPricesComponent} from './daily-crypto-prices/daily-crypto-prices.component';
import {PnlDisplayComponent} from './pnl-display/pnl-display.component';
import {PortfolioCompositionComponent} from './portfolio-composition/portfolio-composition.component';
import {GroupPnlTableComponent} from './group-pnl-table/group-pnl-table.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    DailyCryptoPricesComponent,
    PnlDisplayComponent,
    PortfolioCompositionComponent,
    GroupPnlTableComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
