import { Component, Input, OnInit } from '@angular/core';
import {
  RocCurveDataDto, RocCurvePointDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { RocCurveService } from '../services/roc-curve.service';
import { MessageService, SelectItem } from 'primeng/api';
import { finalize } from 'rxjs/internal/operators';

@Component({
  selector: 'app-roc-curve-chart',
  templateUrl: './roc-curve-chart.component.html',
  styleUrls: ['./roc-curve-chart.component.scss']
})
export class RocCurveChartComponent implements OnInit {

  public dataSet: any;
  public barOptions: any;

  @Input()
  public modelId: number;
  @Input()
  public classValues: SelectItem[] = [];
  @Input()
  public rocCurveService: RocCurveService;
  @Input()
  public selectedClassIndex: number = 0;

  public loading: boolean = false;
  public rocCurveDataDto: RocCurveDataDto;

  public constructor(private messageService: MessageService) {
  }

  public ngOnInit(): void {
    this.initBarOptions();
    this.updateRocCurveData();
  }

  public updateRocCurveData(): void {
    this.loading = true;
    this.rocCurveService.getRocCurveData(this.modelId, this.selectedClassIndex)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (rocCurveDataDto: RocCurveDataDto) => {
          this.rocCurveDataDto = rocCurveDataDto;
          this.dataSet = {
            datasets: [
              {
                fill: false,
                backgroundColor: '#3b7ea5',
                borderColor: 'black',
                data: rocCurveDataDto.rocCurvePoints.map((chartData: RocCurvePointDto) => { return { x: chartData.specificity, y: chartData.sensitivity}}),
                // Hide points
                //radius: 0,
                pointRadius: 0, // Set the radius as needed
                cubicInterpolationMode: 'monotone'
              },
              {
                fill: false,
                backgroundColor: '#3b7ea5',
                borderColor: 'black',
                pointRadius: 7,
                data: [{ x: rocCurveDataDto.optimalPoint.specificity, y: rocCurveDataDto.optimalPoint.sensitivity }],
              }
            ]
          };
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initBarOptions(): void {
    this.barOptions = {
      tooltips: {
        callbacks: {
          title: (data) => {
            if (data[0].datasetIndex == 1) {
              return 'Оптимальный порог';
            }
            return null;
          },
          label: (data) => {
            if (data.datasetIndex == 1) {
              const specificity = 100.0 - data.xLabel;
              const sensitivity = data.yLabel;
              return `Специфичность: ${specificity.toFixed(4)}  Чувствительность: ${sensitivity.toFixed(4)}  Порог: ${this.rocCurveDataDto.optimalPoint.threshold}`;
            }
            return null;
          }
        }
      },
      title: {
        display: true,
        text: 'Данные ROC кривой',
        fontSize: 20
      },
      legend: {
        display: false
      },
      scales: {
        xAxes: [{
          type: 'linear',
          ticks: {
            min: 0,
            beginAtZero: true,
          },
          scaleLabel: {
            display: true,
            labelString: '100 - Специфичность (Specificity), %',
            fontSize: 18
          }
        }],
        yAxes: [{
          type: 'linear',
          ticks: {
            min: 0,
            beginAtZero: true,
          },
          scaleLabel: {
            display: true,
            labelString: 'Чувствительность (Sensitivity), %',
            fontSize: 18
          }
        }]
      }
    };
  }
}
