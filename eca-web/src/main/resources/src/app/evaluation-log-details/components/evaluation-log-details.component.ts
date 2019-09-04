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
                     private route: ActivatedRoute) {
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

  public getEvaluationLogValue(row: string) {
    switch (row) {
      case "evaluationMethod":
        return this.getEvaluationMethod();
      case "evaluationStatus":
        return this.evaluationLogDetails.evaluationStatus.description;
      default:
        const tokens: string[] = row.split(".");
        return tokens.length == 2 ? this.evaluationLogDetails[tokens[0]][tokens[1]] : this.evaluationLogDetails[tokens[0]];
    }
  }

  private initEvaluationLogFields(): void {
    this.evaluationLogFields = [
      { name: "requestId", label: "UUID заявки:" },
      { name: "evaluationStatus", label: "Статус заявки:" },
      { name: "instancesInfo.relationName", label: "Обучающая выборка:" },
      { name: "instancesInfo.numInstances", label: "Число объектов:" },
      { name: "instancesInfo.numAttributes", label: "Число атрибутов:" },
      { name: "instancesInfo.numClasses", label: "Число классов:" },
      { name: "instancesInfo.className", label: "Атрибут класса:" },
      { name: "evaluationMethod", label: "Метод оценки точности:" }
    ];
  }
}
