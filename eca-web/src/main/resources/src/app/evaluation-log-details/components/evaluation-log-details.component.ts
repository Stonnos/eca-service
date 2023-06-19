import { Component, OnInit } from '@angular/core';
import {
  EvaluationLogDetailsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { finalize } from "rxjs/internal/operators";
import { EvaluationLogFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";
import { FieldLink } from "../../common/model/field-link";

@Component({
  selector: 'app-evaluation-log-details',
  templateUrl: './evaluation-log-details.component.html',
  styleUrls: ['./evaluation-log-details.component.scss']
})
export class EvaluationLogDetailsComponent implements OnInit, FieldLink {

  private readonly id: number;

  public evaluationLogFields: any[] = [];
  public loading: boolean = false;

  private modelLoading: boolean = false;

  public linkColumns: string[] = [EvaluationLogFields.MODEL_PATH];

  public evaluationLogDetails: EvaluationLogDetailsDto;

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private fieldService: FieldService) {
    this.id = this.route.snapshot.params.id;
    this.initEvaluationLogFields();
  }

  public ngOnInit(): void {
    this.getEvaluationLogDetails();
  }

  public getEvaluationLogDetails(): void {
    this.loading = true;
    this.classifiersService.getEvaluationLogDetails(this.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (evaluationLogDetails: EvaluationLogDetailsDto) => {
          this.evaluationLogDetails = evaluationLogDetails;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getEvaluationLogValue(field: string) {
    if (this.evaluationLogDetails) {
      if (field == EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION) {
        return Utils.getEvaluationMethodDescription(this.evaluationLogDetails.evaluationMethod,
          this.evaluationLogDetails.numFolds, this.evaluationLogDetails.numTests);
      } else {
        return this.fieldService.getFieldValue(field, this.evaluationLogDetails, Utils.MISSING_VALUE);
      }
    }
    return null;
  }

  public isLink(field: string): boolean {
    return this.linkColumns.includes(field);
  }

  public onLink(field: string) {
    if (field === EvaluationLogFields.MODEL_PATH) {
      this.downloadModel();
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${field} as link`});
    }
  }

  public downloadModel(): void {
    this.modelLoading = true;
    this.classifiersService.downloadModel(this.evaluationLogDetails,
      () => this.modelLoading = false,
      (error) => this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message })
    );
  }

  public showProgressBar(field: string): boolean {
    return field == EvaluationLogFields.MODEL_PATH && this.modelLoading;
  }

  private initEvaluationLogFields(): void {
    this.evaluationLogFields = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки:" },
      { name: EvaluationLogFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки:" },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка:" },
      { name: EvaluationLogFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: EvaluationLogFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: EvaluationLogFields.NUM_CLASSES, label: "Число классов:" },
      { name: EvaluationLogFields.CLASS_NAME, label: "Атрибут класса:" },
      { name: EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности:" },
      { name: EvaluationLogFields.PCT_CORRECT, label: "Точность классификатора:" },
      { name: EvaluationLogFields.EVALUATION_TOTAL_TIME, label: "Время построения модели:" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки:" },
      { name: EvaluationLogFields.START_DATE, label: "Дата начала построения модели:" },
      { name: EvaluationLogFields.END_DATE, label: "Дата окончания построения модели:" },
      { name: EvaluationLogFields.MODEL_PATH, label: "Модель классификатора:" },
      { name: EvaluationLogFields.DELETED_DATE, label: "Дата удаления модели:" },
    ];
  }
}
