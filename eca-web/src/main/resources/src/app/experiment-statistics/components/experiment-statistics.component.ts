import { Component, OnInit } from '@angular/core';
import { ChartDataDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { MessageService } from "primeng/api";
import { DatePipe } from "@angular/common";

@Component({
  selector: 'app-experiment-statistics',
  templateUrl: './experiment-statistics.html',
  styleUrls: ['./experiment-statistics.scss']
})
export class ExperimentStatisticsComponent implements OnInit {

  public dataSet: any;

  public createdDateFrom: Date;

  public createdDateTo: Date;

  public dateFormat: string = "yyyy-MM-dd";

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
          beginAtZero: true
        }
      }]
    }
  };

  public constructor(private experimentsService: ExperimentsService, private messageService: MessageService) {
  }

  public onShow() {
    this.experimentsService.getExperimentTypesStatistics(this.transformDate(this.createdDateFrom), this.transformDate(this.createdDateTo))
      .subscribe((chartData: ChartDataDto[]) => {
        this.dataSet = {
          labels: chartData.map((chartData: ChartDataDto) => `${chartData.label} (${chartData.count})`),
          datasets: [
            {
              backgroundColor: ['#f5191e', '#471cf5', '#20f55a', '#f20df5', '#f5aa17', '#f5eb4a', '#670ff5', '#083e17', '#601813'],
              borderColor: '#a5a5a5',
              data: chartData.map((chartData: ChartDataDto) => chartData.count)
            }
          ]
        };
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  public ngOnInit(): void {
    this.onShow();
  }

  private transformDate(date: Date): string {
    return !!date ? new DatePipe("en-US").transform(date, this.dateFormat) : '';
  }
}
