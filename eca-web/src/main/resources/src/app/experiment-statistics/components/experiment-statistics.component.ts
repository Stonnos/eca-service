import { Component, OnInit } from '@angular/core';
import { ChartDataDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { MessageService } from "primeng/api";
import { DatePipe } from "@angular/common";
import { RandomColor } from "angular-randomcolor";

@Component({
  selector: 'app-experiment-statistics',
  templateUrl: './experiment-statistics.html',
  styleUrls: ['./experiment-statistics.scss']
})
export class ExperimentStatisticsComponent implements OnInit {

  public now: Date = new Date();

  public dataSet: any;

  public total: number = 0;

  public createdDateRange: Date[];

  private datePipe: DatePipe = new DatePipe("en-US");

  private dateFormat: string = "yyyy-MM-dd";

  public barOptions: any = {
    title: {
      display: true,
      text: 'Гистограмма экспериментов',
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

  public constructor(private experimentsService: ExperimentsService, private messageService: MessageService) {
  }

  public onShow() {
    const createdFrom: Date = this.createdDateRange && this.createdDateRange[0];
    const createdTo: Date = this.createdDateRange && this.createdDateRange[1];
    this.experimentsService.getExperimentTypesStatistics(this.transformDate(createdFrom), this.transformDate(createdTo))
      .subscribe((chartData: ChartDataDto[]) => {
      this.total = chartData.map((chartData: ChartDataDto) => chartData.count).reduce((sum, current) => sum + current);
        this.dataSet = {
          labels: chartData.map((chartData: ChartDataDto) => `${chartData.label} (${chartData.count})`),
          datasets: [
            {
              backgroundColor: chartData.map((chartData: ChartDataDto) => RandomColor.generateColor()),
              borderColor: '#a5a5a5',
              data: chartData.map((chartData: ChartDataDto) => chartData.count)
            }
          ]
        };
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public ngOnInit(): void {
    this.onShow();
  }

  private transformDate(date: Date): string {
    return !!date ? this.datePipe.transform(date, this.dateFormat) : '';
  }
}
