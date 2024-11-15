import { Component, OnInit } from '@angular/core';
import { NGX_ECHARTS_CONFIG, NgxEchartsDirective } from "ngx-echarts";
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/envrionment.dev';
import {CurrencyPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-portfolio-value-barchart',
  standalone: true,
  imports: [
    NgxEchartsDirective,
    CurrencyPipe,
    NgIf
  ],
  templateUrl: './portfolio-value-barchart.component.html',
  styleUrl: './portfolio-value-barchart.component.css',
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useValue: {
        echarts: () => import('echarts')
      }
    }
  ]
})
export class PortfolioValueBarchartComponent implements OnInit {
  chartOptions: any;
  totalProfit: number | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadPortfolioValues();
  }

  loadPortfolioValues(): void {
    this.http
      .get<{ [key: string]: number }>(`${environment.apiBaseUrl}/dashboard/portfolio-values`)
      .subscribe({
        next: (response) => {
          const keys = Object.keys(response);
          const values = Object.values(response);

          // Calculate total profit
          this.totalProfit = values[0] - values[1];

          this.chartOptions = {
            title: {
              text: 'Portfolio Value',
              left: 'center'
            },
            tooltip: {
              trigger: 'axis',
              formatter: '{b}: {c}',
              axisPointer: {
                type: 'shadow'
              }
            },
            toolbox: {
              show: true,
              feature: {
                mark: { show: true },
                dataView: { show: true, readOnly: true },
                restore: { show: true }
              }
            },
            xAxis: {
              type: 'category',
              data: keys,
              axisLabel: {
                rotate: 0,
                formatter: (value: string) => (value.length > 40 ? `${value.slice(0, 10)}...` : value)
              }
            },
            yAxis: {
              type: 'value',
              axisLabel: {
                formatter: '{value}'
              }
            },
            series: [
              {
                name: 'Portfolio Value',
                type: 'bar',
                data: values,
                itemStyle: {
                  color: (params: any) => {
                    return params.dataIndex === 0 ? '#9e9e9e' : '#4caf50'; // Grey for Original, Green for Current
                  }
                }
              }
            ]
          };
        },
        error: (err) => {
          console.error('Failed to load portfolio values', err);
        }
      });
  }
}
