import { Component, Input, OnInit } from '@angular/core';
import { MessageService } from "primeng/api";
import { DatePipe } from "@angular/common";
import { Observable } from "rxjs/internal/Observable";
import { ChartDataDto, ChartDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-chart-bar-statistics',
  templateUrl: './chart-bar-statistics.component.html',
  styleUrls: ['./chart-bar-statistics.component.scss']
})
export class ChartBarStatisticsComponent implements OnInit {

  public now: Date = new Date();

  public dataSet: any;

  public total: number;

  public createdDateRange: Date[];

  @Input()
  public chartTitle: string;
  @Input()
  public totalCountTitle: string;
  @Input()
  public dateLabelTitle: string;
  @Input()
  public loadData: (from: string, to: string) => Observable<ChartDto>;

  private datePipe: DatePipe = new DatePipe("en-US");

  private dateFormat: string = "yyyy-MM-dd";

  public barOptions: any;

  public constructor(private messageService: MessageService) {
  }

  public onShow() {
    const createdFrom: Date = this.createdDateRange && this.createdDateRange[0];
    const createdTo: Date = this.createdDateRange && this.createdDateRange[1];
    this.loadData(this.transformDate(createdFrom), this.transformDate(createdTo))
      .subscribe({
        next: (chartData: ChartDto) => {
          this.total = chartData.total;
          this.dataSet = {
            labels: chartData.dataItems.map((chartData: ChartDataDto) => `${chartData.label} (${chartData.count})`),
            datasets: [
              {
                backgroundColor: '#3b7ea5',
                borderColor: '#a5a5a5',
                data: chartData.dataItems.map((chartData: ChartDataDto) => chartData.count)
              }
            ]
          };
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public ngOnInit(): void {
    this.initBarOptions();
    this.onShow();
  }

  private initBarOptions(): void {
    this.barOptions = {
      title: {
        display: true,
        text: this.chartTitle,
        fontSize: 20
      },
      legend: {
        display: false
      },
      scales: {
        xAxes: [{
          ticks: {
            min: 0,
            beginAtZero: true,
            precision: 0
          }
        }]
      }
    };
  }

  private transformDate(date: Date): string {
    return date ? this.datePipe.transform(date, this.dateFormat) : '';
  }
}
