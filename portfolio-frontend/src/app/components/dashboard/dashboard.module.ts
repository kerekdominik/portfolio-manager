import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DashboardRoutingModule} from './dasboard-routing.module';
import {DailyCryptoPricesComponent} from './daily-crypto-prices/daily-crypto-prices.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    DailyCryptoPricesComponent
  ]
})
export class DashboardModule { }
