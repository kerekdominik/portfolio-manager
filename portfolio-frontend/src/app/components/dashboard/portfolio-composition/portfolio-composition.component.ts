import {Component, OnInit} from '@angular/core';
import {NGX_ECHARTS_CONFIG, NgxEchartsDirective} from 'ngx-echarts';
import {NgIf} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/envrionment.dev';

@Component({
  selector: 'app-portfolio-composition',
  standalone: true,
  imports: [
    NgxEchartsDirective,
    NgIf
  ],
  templateUrl: './portfolio-composition.component.html',
  styleUrls: ['./portfolio-composition.component.css'],
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useValue: {
        echarts: () => import('echarts')
      }
    }
  ]
})
export class PortfolioCompositionComponent implements OnInit {
  chartOptions: any;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadComposition();
  }

  loadComposition(): void {
    this.http.get<{ [key: string]: number }>(`${environment.apiBaseUrl}/dashboard/portfolio-composition`).subscribe({
      next: (response) => {
        this.chartOptions = {
          title: {
            text: 'Portfolio Composition',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
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
              name: 'Assets',
              type: 'pie',
              radius: '50%',
              data: Object.entries(response).map(([key, value]) => ({ value, name: key })),
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        };
      },
      error: (err) => {
        console.error('Failed to load portfolio composition', err);
      }
    });
  }
}
