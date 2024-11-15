import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/envrionment.dev';
import {NGX_ECHARTS_CONFIG, NgxEchartsDirective} from 'ngx-echarts';

@Component({
  selector: 'app-pnl-assets-chart',
  standalone: true,
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './pnl-assets-chart.component.html',
  styleUrl: './pnl-assets-chart.component.css',
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useValue: {
        echarts: () => import('echarts')
      }
    }
  ]
})
export class PnlAssetsChartComponent implements OnInit {
  chartOptions: any;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadAssetPnlData();
  }

  loadAssetPnlData(): void {
    this.http.get<{ [key: string]: number }>(`${environment.apiBaseUrl}/dashboard/pnl-assets`).subscribe({
      next: (response) => {
        // Filter out negative values
        const chartData = Object.entries(response)
          .filter(([_, value]) => value >= 0) // Only include non-negative values
          .map(([name, value]) => ({
            name,
            value: Math.abs(value)
          }));

        // Sort the data by absolute value (largest first)
        chartData.sort((a, b) => b.value - a.value);

        // Configure chart options
        this.chartOptions = {
          title: {
            text: 'Profit Distribution by Asset',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            left: 'center',
            top: 'bottom',
            data: chartData.map((item) => item.name)
          },
          toolbox: {
            show: true,
            feature: {
              mark: { show: true },
              dataView: { show: true, readOnly: true },
              restore: { show: true }
            }
          },
          series: [
            {
              name: 'PNL',
              type: 'pie',
              radius: [20, 140],
              center: ['50%', '50%'],
              roseType: 'radius',
              itemStyle: {
                borderRadius: 5
              },
              label: {
                show: true
              },
              data: chartData
            }
          ]
        };
      },
      error: (err) => {
        console.error('Failed to load asset PNL data:', err);
      }
    });
  }
}
