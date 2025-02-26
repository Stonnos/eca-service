import { Component, OnInit } from '@angular/core';
import {
  ExperimentResultsDetailsDto, AttributeValueMetaInfoDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService, SelectItem } from 'primeng/api';
import { ActivatedRoute } from "@angular/router";
import { InstancesInfoService } from '../../common/instances-info/services/instances-info.service';
import { ExperimentsService } from '../../experiments/services/experiments.service';

@Component({
  selector: 'app-experiment-results-roc-curve',
  templateUrl: './experiment-results-roc-curve.component.html',
  styleUrls: ['./experiment-results-roc-curve.component.scss']
})
export class ExperimentResultsRocCurveComponent implements OnInit {

  private id: number;

  public experimentResultsDetailsDto: ExperimentResultsDetailsDto;

  public classValues: SelectItem[] = [];

  public modelDeletedMessageText: string = 'Невозможно получить данные ROC - кривой, т.к. модель эксперимента была удалена';

  public constructor(private experimentService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private instancesInfoService: InstancesInfoService) {
    this.id = this.route.snapshot.params.id;
  }

  public ngOnInit(): void {
    this.getExperimentResultsDetails();
  }

  public getClassValues(): void {
    this.instancesInfoService.getClassValues(this.experimentResultsDetailsDto.experimentDto.instancesInfo.id)
      .subscribe({
        next: (classValues: AttributeValueMetaInfoDto[]) => {
          this.classValues = classValues.map((attributeValueMetaInfoDto: AttributeValueMetaInfoDto) => {
            return { label: attributeValueMetaInfoDto.value, value: attributeValueMetaInfoDto.index};
          });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getExperimentResultsDetails(): void {
    this.experimentService.getExperimentResultsDetails(this.id)
      .subscribe({
        next: (experimentResultsDetailsDto: ExperimentResultsDetailsDto) => {
          this.experimentResultsDetailsDto = experimentResultsDetailsDto;
          this.getClassValues();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
