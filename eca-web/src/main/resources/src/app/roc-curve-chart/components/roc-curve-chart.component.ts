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
    }
  };

  public constructor() {
    this.dataSet = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'First Dataset',
          data: [65, 59, 80, 81, 56, 55, 40],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'Second Dataset',
          data: [28, 48, 40, 19, 86, 27, 90],
          fill: false,
          borderColor: '#565656'
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
