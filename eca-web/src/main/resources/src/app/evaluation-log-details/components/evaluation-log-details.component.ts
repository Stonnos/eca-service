import { Component, OnInit } from '@angular/core';
import {
  EnumDto,
  EvaluationLogDetailsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { finalize } from "rxjs/internal/operators";
import { EvaluationLogFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";

@Component({
  selector: 'app-evaluation-log-details',
  templateUrl: './evaluation-log-details.component.html',
  styleUrls: ['./evaluation-log-details.component.scss']
})
export class EvaluationLogDetailsComponent implements OnInit {

  private readonly requestId: string;

  public evaluationLogFields: any[] = [];
  public loading: boolean = false;

  public evaluationLogDetails: EvaluationLogDetailsDto;

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private fieldService: FieldService) {
    this.requestId = this.route.snapshot.params.id;
    this.initEvaluationLogFields();
  }

  public ngOnInit(): void {
    this.getEvaluationLogDetails();
  }

  public getEvaluationLogDetails(): void {
    this.loading = true;
    this.classifiersService.getEvaluationLogDetails(this.requestId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((evaluationLogDetails: EvaluationLogDetailsDto) => {
        this.evaluationLogDetails = evaluationLogDetails;
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getEvaluationMethod(): string {
    const evaluationMethod: EnumDto = this.evaluationLogDetails.evaluationMethod;
    if (evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
      if (this.evaluationLogDetails.numTests == 1) {
        return `${this.evaluationLogDetails.evaluationMethod.description} (${this.evaluationLogDetails.numFolds} блочная)`;
      } else {
        return `${this.evaluationLogDetails.evaluationMethod.description} (${this.evaluationLogDetails.numTests}×${this.evaluationLogDetails.numFolds} блочная)`;
      }
    } else {
      return this.evaluationLogDetails.evaluationMethod.description;
    }
  }

  public getEvaluationLogValue(field: string) {
    if (field == EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION) {
      return this.getEvaluationMethod();
    } else {
      return this.fieldService.getFieldValue(field, this.evaluationLogDetails, Utils.MISSING_VALUE);
    }
  }

  private initEvaluationLogFields(): void {
    this.evaluationLogFields = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки:" },
      { name: EvaluationLogFields.EVALUATION_STATUS_DESCRIPTION, label: "Статус заявки:" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки:" },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка:" },
      { name: EvaluationLogFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: EvaluationLogFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: EvaluationLogFields.NUM_CLASSES, label: "Число классов:" },
      { name: EvaluationLogFields.CLASS_NAME, label: "Атрибут класса:" },
      { name: EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности:" }
    ];
  }
}
