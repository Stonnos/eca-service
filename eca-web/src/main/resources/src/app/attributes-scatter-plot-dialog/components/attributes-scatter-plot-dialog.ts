import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AttributeDto,
  AttributeValueDto,
  AttributesScatterPlotDto,
  AttributesScatterPlotDataSetDto,
  AttributesScatterPlotDataItemDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseDialog } from '../../common/dialog/base-dialog';
import { InstancesService } from '../../instances/services/instances.service';
import { finalize } from 'rxjs/internal/operators';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-attributes-scatter-plot-dialog',
  templateUrl: './attributes-scatter-plot-dialog.html',
  styleUrls: ['./attributes-scatter-plot-dialog.scss']
})
export class AttributesScatterPlotDialog implements BaseDialog, OnInit {

  public scatterPlotBarOptions: any;

  public scatterPlotDataSet: any;

  public selectedXAttribute: AttributeDto;

  public selectedYAttribute: AttributeDto;

  public loading: boolean = false;

  @Input()
  public visible: boolean = false;
  @Input()
  public instancesId: number;
  @Input()
  public attributes: AttributeDto[] = [];

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  private colors = [
    "#3366CC", "#DC3912", "#FF9900", "#109618", "#990099", "#3B3EAC", "#0099C6",
    "#DD4477", "#66AA00", "#B82E2E", "#316395", "#994499", "#22AA99", "#AAAA11",
    "#6633CC", "#E67300", "#8B0707", "#329262", "#5574A6", "#651067"
  ];

  public constructor(private instancesService: InstancesService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
    this.initDefaultOptions();
  }

  public hide() {
    this.visibilityChange.emit(false);
  }

  public fetchScatterPlot(): void {
    this.loading = true;
    this.instancesService.getAttributesScatterPlot(this.instancesId, this.selectedXAttribute.id, this.selectedYAttribute.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (scatterPlotDto: AttributesScatterPlotDto) => {
          this.updateScatterPlotOptions(scatterPlotDto);
          this.updateScatterPlotDataSets(scatterPlotDto);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private updateScatterPlotOptions(scatterPlotDto: AttributesScatterPlotDto): void {
    this.scatterPlotBarOptions = {
      title: {
        display: true,
        text: 'Диаграмма рассеяния',
        fontSize: 20
      },
      legend: {
        display: true
      },
      scales: {
        xAxes: this.getAxisData(scatterPlotDto.xaxisAttribute),
        yAxes: this.getAxisData(scatterPlotDto.yaxisAttribute)
      }
    };
  }

  private updateScatterPlotDataSets(scatterPlotDto: AttributesScatterPlotDto): void {
    const datasets = scatterPlotDto.dataSets.map((dataSetDto: AttributesScatterPlotDataSetDto, i: number) => {
      return {
        label: dataSetDto.classValue,
        backgroundColor: this.colors[i % this.colors.length],
        borderColor: this.colors[i % this.colors.length],
        data: dataSetDto.items.map((item: AttributesScatterPlotDataItemDto) => {
          const x = scatterPlotDto.xaxisAttribute.type.value == 'NOMINAL' ? item.xlabel : item.xvalue;
          const y = scatterPlotDto.yaxisAttribute.type.value == 'NOMINAL' ? item.ylabel : item.yvalue;
          return { x: x, y: y};
        })
      }
    });
    this.scatterPlotDataSet = {
      datasets: datasets
    };
  }

  private getAxisData(attribute: AttributeDto): any {
    if (attribute.type.value == 'NOMINAL') {
      return [{
        type: 'category',
        labels: attribute.values.map((v: AttributeValueDto) => v.value),
        scaleLabel: {
          display: true,
          labelString: attribute.name,
          fontSize: 18
        }
      }];
    } else {
      return [{
        type: 'linear',
        ticks: {
          min: 0,
          beginAtZero: true,
        },
        scaleLabel: {
          display: true,
          labelString: attribute.name,
          fontSize: 18
        }
      }];
    }
  }

  private initDefaultOptions(): void {
    this.scatterPlotBarOptions = {
      title: {
        display: true,
        text: 'Диаграмма рассеяния',
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
        }],
        yAxes: [{
          type: 'linear',
          ticks: {
            min: 0,
            beginAtZero: true,
          },
        }]
      }
    };
  }
}
