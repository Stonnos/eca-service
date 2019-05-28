import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { RocCurveDataDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";

@Component({
  selector: 'app-roc-curve-chart',
  templateUrl: './roc-curve-chart.html',
  styleUrls: ['./roc-curve-chart.scss']
})
export class RocCurveChartComponent implements OnInit, OnChanges {

  @Input()
  public rocCurveData: RocCurveDataDto[] = [];

  public dataSet: any;
  public classValues: SelectItem[] = [];
  public selectedClassValue: any;

  public options: any = {
    title: {
      display: true,
      text: 'График ROC - кривой',
      fontSize: 20
    },
    legend: {
      position: 'bottom'
    },
    scales: {
      xAxes: [{
        type: 'linear',
        ticks: {
          min: 0,
          beginAtZero: true
        }
      }]
    }
  };

  public constructor() {
    //let xAxis = [1,2,20,40,50];
    this.dataSet = {
      //labels: xAxis,
      datasets: [
        {
          label: 'First Dataset',
          data: [{x: 1, y: 12}, {x: 1, y: 12}, {x: 4, y: 6}, {x: 5, y: 16}],
          fill: false,
          borderColor: '#4bc0c0',
          lineTension: 0
        },
        {
          label: 'Second Dataset',
          data: [{x: 4, y: 2}, {x: 7, y: 18}, {x: 22, y: 61}, {x: 32, y: 14}],
          fill: false,
          borderColor: '#565656',
          lineTension: 0
        }
      ]
    };
  }

  public ngOnInit(): void {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    this.initClassValues();
  }

  private initClassValues(): void {
    this.classValues = [];
    this.classValues.push({label: 'Все классы', value: 'all'});
    if (!!this.rocCurveData && this.rocCurveData.length > 0) {
      this.rocCurveData.forEach((data: RocCurveDataDto) => {
        this.classValues.push({ label: data.classValue, value: data.classValue });
      });
    }
  }
}
