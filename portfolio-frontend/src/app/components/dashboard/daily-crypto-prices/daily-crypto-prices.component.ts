import { Component, OnInit } from '@angular/core';
import { CryptoService } from '../../services/crypto.service';
import {CurrencyPipe, NgClass, NgIf} from '@angular/common';
import { MatCard, MatCardContent, MatCardHeader, MatCardModule } from '@angular/material/card';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-daily-prices',
  standalone: true,
  imports: [
    CurrencyPipe,
    NgIf,
    MatCard,
    MatCardModule,
    MatCardHeader,
    MatCardContent,
    MatProgressSpinner,
    NgClass
  ],
  templateUrl: './daily-crypto-prices.component.html',
  styleUrl: './daily-crypto-prices.component.css'
})
export class DailyCryptoPricesComponent implements OnInit {
  btcCurrentPrice: number | null = null;
  btcOneYearAgoPrice: number | null = null;
  ethCurrentPrice: number | null = null;
  ethOneYearAgoPrice: number | null = null;

  constructor(private priceService: CryptoService) {}

  ngOnInit(): void {
    this.fetchBtcCurrentPrice();
    this.fetchBtcOneYearAgoPrice();
    this.fetchEthCurrentPrice();
    this.fetchEthOneYearAgoPrice();
  }

  private fetchBtcCurrentPrice(): void {
    this.priceService.getCurrentPrice('bitcoin').subscribe({
      next: (response) => this.btcCurrentPrice = response.price,
      error: (err) => console.error('Error on current BTC price request:', err)
    });
  }

  private fetchBtcOneYearAgoPrice(): void {
    this.priceService.getOneYearAgoPrice('bitcoin', this.getOneYearAgoDate()).subscribe({
      next: (response) => this.btcOneYearAgoPrice = response.price,
      error: (err) => console.error('Error on historical BTC price request:', err)
    });
  }

  private fetchEthCurrentPrice(): void {
    this.priceService.getCurrentPrice('ethereum').subscribe({
      next: (response) => this.ethCurrentPrice = response.price,
      error: (err) => console.error('Error on current ETH price request:', err)
    });
  }

  private fetchEthOneYearAgoPrice(): void {
    this.priceService.getOneYearAgoPrice('ethereum', this.getOneYearAgoDate()).subscribe({
      next: (response) => this.ethOneYearAgoPrice = response.price,
      error: (err) => console.error('Error on historical ETH price request:', err)
    });
  }

  private getOneYearAgoDate(): string {
    const date = new Date();
    date.setFullYear(date.getFullYear() - 1);
    return date.toISOString().split('T')[0];
  }
}
