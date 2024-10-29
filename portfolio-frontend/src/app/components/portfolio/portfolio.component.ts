import { Component } from '@angular/core';
import {MatGridList, MatGridTile} from '@angular/material/grid-list';
import {MatCard, MatCardTitle} from '@angular/material/card';
import {CryptoTableComponent} from './crypto-table/crypto-table.component';
import {StockTableComponent} from './stock-table/stock-table.component';
import {GroupsTableComponent} from './groups-table/groups-table.component';

@Component({
  selector: 'app-portfolio',
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    MatCard,
    MatCardTitle,
    CryptoTableComponent,
    StockTableComponent,
    GroupsTableComponent
  ],
  templateUrl: './portfolio.component.html',
  styleUrl: './portfolio.component.css'
})
export class PortfolioComponent {

}
