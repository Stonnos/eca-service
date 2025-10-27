import { Component, Input, OnInit } from '@angular/core';
import {
  RocCurveDataDto, RocCurvePointDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { RocCurveService } from '../services/roc-curve.service';
import { MessageService, SelectItem } from 'primeng/api';
import { finalize } from 'rxjs/internal/operators';
import { forkJoin } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';

@Component({
  selector: 'app-roc-curve-chart',
  templateUrl: './roc-curve-chart.component.html',
  styleUrls: ['./roc-curve-chart.component.scss']
})
export class RocCurveChartComponent implements OnInit {

  public dataSet: any;
  public barOptions: any;
  public plugins: any[];

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

  private colors = [
    "#3366CC", "#DC3912", "#FF9900", "#109618", "#990099", "#3B3EAC", "#0099C6",
    "#DD4477", "#66AA00", "#B82E2E", "#316395", "#994499", "#22AA99", "#AAAA11",
    "#6633CC", "#E67300", "#8B0707", "#329262", "#5574A6", "#651067"
  ];

  public constructor(private messageService: MessageService) {
  }

  public ngOnInit(): void {
    this.initPlugins();
    this.initBarOptions();
    this.updateRocCurveData();
  }

  public updateRocCurveData(): void {
    if (this.selectedClassIndex < 0) {
      this.getRocCurveDataAllClasses();
    } else {
      this.getRocCurveDataByClassIndex();
    }
  }

  private getRocCurveDataAllClasses(): void {
    this.loading = true;
    const observables: Observable<RocCurveDataDto>[] = this.classValues
      .filter((selectItem: SelectItem) => selectItem.value >= 0)
      .map((selectItem: SelectItem) => this.rocCurveService.getRocCurveData(this.modelId, selectItem.value));
    forkJoin(observables)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (results: RocCurveDataDto[]) => {
          const datasets = results.map((rocCurveDataDto: RocCurveDataDto, i: number) => {
            return {
              label: rocCurveDataDto.classValue,
              fill: false,
              backgroundColor: this.colors[i % this.colors.length],
              borderColor: this.colors[i % this.colors.length],
              data: rocCurveDataDto.rocCurvePoints.map((chartData: RocCurvePointDto) => { return { x: chartData.specificity, y: chartData.sensitivity}}),
              // Hide points
              pointRadius: 0, // Set the radius as needed
              cubicInterpolationMode: 'monotone'
            }
          });
          this.dataSet = {
            datasets: datasets
          };
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private getRocCurveDataByClassIndex(): void {
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
                label: rocCurveDataDto.classValue,
                fill: false,
                backgroundColor: 'black',
                borderColor: 'black',
                data: rocCurveDataDto.rocCurvePoints.map((chartData: RocCurvePointDto) => { return { x: chartData.specificity, y: chartData.sensitivity}}),
                // Hide points
                pointRadius: 0, // Set the radius as needed
                cubicInterpolationMode: 'monotone'
              },
              {
                label: 'Точка оптимального порога',
                fill: false,
                backgroundColor: '#3b7ea5',
                borderColor: '#3b7ea5',
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

  private initPlugins(): void {
    const canvasBackgroundPlugin = {
      id: 'canvasBackground',
      beforeDraw: (chart) => {
        const ctx = chart.ctx;
        ctx.fillStyle = "white";
        ctx.fillRect(0, 0, chart.width, chart.height);
      }
    };
    this.plugins = [canvasBackgroundPlugin];
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
        display: true
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
