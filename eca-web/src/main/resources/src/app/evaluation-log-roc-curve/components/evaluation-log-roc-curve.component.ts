import { Component, OnInit } from '@angular/core';
import {
  EvaluationLogDetailsDto, AttributeValueMetaInfoDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";
import { MessageService, SelectItem } from 'primeng/api';
import { ActivatedRoute } from "@angular/router";
import { InstancesInfoService } from '../../common/instances-info/services/instances-info.service';

@Component({
  selector: 'app-evaluation-log-roc-curve',
  templateUrl: './evaluation-log-roc-curve.component.html',
  styleUrls: ['./evaluation-log-roc-curve.component.scss']
})
export class EvaluationLogRocCurveComponent implements OnInit {

  private id: number;

  public evaluationLogDetails: EvaluationLogDetailsDto;

  public classValues: SelectItem[] = [];

  public requestInProgressMessageText: string = 'Идет построение модели классификатора...';

  public errorRequestMessageText: string = ' Невозможно получить данные ROC - кривой, т.к. произошла ошибка при построении модели классификатора';

  public modelDeletedMessageText: string = 'Невозможно получить данные ROC - кривой, т.к. модель классификатора была удалена';

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private instancesInfoService: InstancesInfoService) {
    this.id = this.route.snapshot.params.id;
  }

  public ngOnInit(): void {
    this.getEvaluationLogDetails();
  }

  public getClassValues(): void {
    this.instancesInfoService.getClassValues(this.evaluationLogDetails.instancesInfo.id)
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

  public getEvaluationLogDetails(): void {
    this.classifiersService.getEvaluationLogDetails(this.id)
      .subscribe({
        next: (evaluationLogDetails: EvaluationLogDetailsDto) => {
          this.evaluationLogDetails = evaluationLogDetails;
          this.getClassValues();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
