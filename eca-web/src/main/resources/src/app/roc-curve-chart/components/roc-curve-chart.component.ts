import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { RocCurveDataDto, RocCurvePointDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";
import { Point } from "../model/point.model";

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

  public ngOnInit(): void {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    this.initClassValues();
    this.createRocCurveChart(null);
  }

  public onChangeClass(event): void {
    console.log(event.value);
    this.createRocCurveChart(event.value.value);
  }

  private initClassValues(): void {
    this.classValues = [];
    this.classValues.push({label: 'Все классы', value: null});
    if (!!this.rocCurveData && this.rocCurveData.length > 0) {
      this.rocCurveData.forEach((data: RocCurveDataDto) => {
        this.classValues.push({ label: data.classValue, value: data.classValue });
      });
    }
  }

  private createRocCurveChart(classValue: string): void {
    let dataSets = [];
    if (!!this.rocCurveData && this.rocCurveData.length > 0) {
      if (classValue == null) {
        this.rocCurveData.forEach((data: RocCurveDataDto) => {
          dataSets.push(this.createDataSet(data));
        });
      } else {
        let found: RocCurveDataDto[] = this.rocCurveData.filter((data: RocCurveDataDto) => data.classValue == classValue);
        if (!!found && found.length > 0) {
          dataSets.push(this.createDataSet(found[0]));
        }
      }
    }
    this.dataSet = {
      datasets: dataSets
    };
  }

  private createRocCurvePoints(rocCurveData: RocCurveDataDto): Point[] {
    if (!!rocCurveData.points && rocCurveData.points.length > 0) {
      return rocCurveData.points.map((point: RocCurvePointDto) => {
        return new Point(point.xvalue, point.yvalue);
      });
    }
    return [];
  }

  private createDataSet(data: RocCurveDataDto): any {
    return {
      label: data.classValue,
      data: this.createRocCurvePoints(data),
      fill: false,
      borderColor: '#4bc0c0',
      lineTension: 0
    };
  }
}
