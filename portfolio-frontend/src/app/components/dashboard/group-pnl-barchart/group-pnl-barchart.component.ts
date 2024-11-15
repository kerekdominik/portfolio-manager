import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/envrionment.dev';
import {HttpClient} from '@angular/common/http';
import {NGX_ECHARTS_CONFIG, NgxEchartsDirective} from 'ngx-echarts';

@Component({
  selector: 'app-group-pnl-barchart',
  standalone: true,
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './group-pnl-barchart.component.html',
  styleUrl: './group-pnl-barchart.component.css',
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useValue: {
        echarts: () => import('echarts')
      }
    }
  ]
})
export class GroupPnlBarChartComponent implements OnInit {
  chartOptions: any;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadGroupPnl();
  }

  loadGroupPnl(): void {
    this.http
      .get<{ [key: string]: number }>(`${environment.apiBaseUrl}/dashboard/group-pnl`)
      .subscribe({
        next: (response) => {
          const groupNames = Object.keys(response);
          const pnlValues = Object.values(response);

          this.chartOptions = {
            title: {
              text: 'Group PNL',
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
              data: groupNames,
              axisLabel: {
                rotate: 45, // Rotate labels for better readability
                formatter: (value: string) => (value.length > 10 ? `${value.slice(0, 10)}...` : value)
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
                name: 'PNL',
                type: 'bar',
                data: pnlValues,
                itemStyle: {
                  color: (params: any) =>
                    params.value >= 0 ? '#4caf50' : '#f44336' // Green for positive, red for negative
                }
              }
            ]
          };
        },
        error: (err) => {
          console.error('Failed to load group PNL', err);
        }
      });
  }
}
