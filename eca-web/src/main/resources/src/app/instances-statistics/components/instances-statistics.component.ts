import { Component, Injector, OnInit } from '@angular/core';
import {
  AttributeDto, AttributeStatisticsDto, FrequencyDiagramDataDto,
  InstancesStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { InstancesService } from "../../instances/services/instances.service";
import { ActivatedRoute } from "@angular/router";
import { finalize } from "rxjs/internal/operators";

@Component({
  selector: 'app-instances-statistics',
  templateUrl: './instances-statistics.component.html',
  styleUrls: ['./instances-statistics.component.scss']
})
export class InstancesStatisticsComponent implements OnInit {

  private readonly id: number;

  public loading: boolean = false;
  public attributes: AttributeDto[] = [];
  public selectedAttribute: AttributeDto;
  public instancesStatisticsDto: InstancesStatisticsDto;

  public attributeStatistics: AttributeStatisticsDto;

  public attributeCountingStatsBarOptions: any;
  public attributeCountingStatsDataSet: any;

  public frequencyDiagramBarOptions: any;

  public frequencyDiagramDataSet: any;

  public constructor(private injector: Injector,
                     private instancesService: InstancesService,
                     private messageService: MessageService,
                     private route: ActivatedRoute) {
    this.id = this.route.snapshot.params.id;
  }

  public onSelectAttribute(event): void {
    this.getAttributeStatistics(event.value.id);
  }

  public ngOnInit() {
    this.getInstancesStatistics();
    this.getAttributes();
  }

  public getInstancesStatistics(): void {
    this.loading = true;
    this.instancesService.getInstancesStatistics(this.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (statisticsDto: InstancesStatisticsDto) => {
          this.instancesStatisticsDto = statisticsDto;
          this.initAttributesCountingStatsBarOptions();
          this.initAttributesCountingStatsDataSet();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getAttributes(): void {
    this.instancesService.getAttributes(this.id)
      .subscribe({
        next: (attributes: AttributeDto[]) => {
          this.attributes = attributes;
          this.selectedAttribute = attributes[0];
          this.getAttributeStatistics(this.selectedAttribute.id);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private getAttributeStatistics(id: number): void {
    this.loading = true;
    this.instancesService.getAttributeStatistics(id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (statisticsDto: AttributeStatisticsDto) => {
          this.attributeStatistics = statisticsDto;
          this.updateFrequencyDiagramBarOptions();
          this.updateFrequencyDiagramDataSet();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initAttributesCountingStatsBarOptions(): void {
    this.attributeCountingStatsBarOptions = {
      title: {
        display: true,
        text: `Число атрибутов: ${this.instancesStatisticsDto.numAttributes}`,
        fontSize: 20
      },
      legend: {
        display: true,
        position: 'bottom'
      },
    };
  }

  private initAttributesCountingStatsDataSet(): void {
    this.attributeCountingStatsDataSet = {
      labels: [
        'Числовой',
        'Категориальный',
        'Дата и время'
      ],
      datasets: [
        {
          backgroundColor: ['lightgreen', 'orange', 'blue'],
          borderColor: '#a5a5a5',
          data: [
            this.instancesStatisticsDto.numNumericAttributes,
            this.instancesStatisticsDto.numNominalAttributes,
            this.instancesStatisticsDto.numDateAttributes
          ]
        }
      ]
    };
  }

  private updateFrequencyDiagramBarOptions(): void {
    this.frequencyDiagramBarOptions = {
      title: {
        display: true,
        text: `Гистограмма частот атрибута: ${this.attributeStatistics.name}`,
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
        }],
        yAxes: [{
          ticks: {
            min: 0,
            beginAtZero: true,
            precision: 0
          }
        }]
      }
    };
  }

  private updateFrequencyDiagramDataSet(): void {
    this.frequencyDiagramDataSet = {
      labels: this.createFrequencyDiagramLabels(),
      datasets: [
        {
          backgroundColor: '#3b7ea5',
          borderColor: '#a5a5a5',
          data: this.attributeStatistics.frequencyDiagramValues.map((frequencyDiagramDataDto: FrequencyDiagramDataDto) => frequencyDiagramDataDto.frequency)
        }
      ]
    };
  }

  private createFrequencyDiagramLabels(): string[] {
    if (this.attributeStatistics.type.value == 'NOMINAL') {
      return this.attributeStatistics.frequencyDiagramValues.map((frequencyDiagramDataDto: FrequencyDiagramDataDto) => frequencyDiagramDataDto.code)
    } else {
      const firstInterval = this.attributeStatistics.frequencyDiagramValues[0];
      let labels: string[] = [`[${firstInterval.lowerBound}, ${firstInterval.upperBound}]`];
      for (let i = 1; i < this.attributeStatistics.frequencyDiagramValues.length; i++) {
        const interval = this.attributeStatistics.frequencyDiagramValues[i];
        labels.push(`(${interval.lowerBound}, ${interval.upperBound}]`)
      }
      return labels;
    }
  }
}
