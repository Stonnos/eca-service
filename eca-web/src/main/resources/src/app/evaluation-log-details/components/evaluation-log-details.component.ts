import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  EvaluationLogDetailsDto, PushRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";
import { MessageService } from "primeng/api";
import { ActivatedRoute, NavigationEnd, Router, RouterEvent } from "@angular/router";
import { filter, finalize} from "rxjs/internal/operators";
import { EvaluationLogFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";
import { FieldLink } from "../../common/model/field-link";
import { Subscription } from "rxjs";
import { Logger} from "../../common/util/logging";
import { PushMessageType } from "../../common/util/push-message.type";
import { PushVariables } from "../../common/util/push-variables";
import { PushService } from "../../common/push/push.service";

@Component({
  selector: 'app-evaluation-log-details',
  templateUrl: './evaluation-log-details.component.html',
  styleUrls: ['./evaluation-log-details.component.scss']
})
export class EvaluationLogDetailsComponent implements OnInit, OnDestroy, FieldLink {

  private readonly finalRequestStatuses = ['FINISHED', 'ERROR', 'TIMEOUT'];

  private id: number;

  public evaluationLogFields: any[] = [];
  public loading: boolean = false;

  private modelLoading: boolean = false;

  public linkColumns: string[] = [EvaluationLogFields.MODEL_PATH];

  public evaluationLogDetails: EvaluationLogDetailsDto;

  private routeUpdateSubscription: Subscription;

  public blink = false;

  private evaluationUpdatesSubscription: Subscription;

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private router: Router,
                     private pushService: PushService,
                     private fieldService: FieldService) {
    this.id = this.route.snapshot.params.id;
    this.initEvaluationLogFields();
  }

  public ngOnInit(): void {
    this.getEvaluationLogDetails(true);
    this.subscribeForRouteChanges();
  }

  public ngOnDestroy(): void {
    this.routeUpdateSubscription.unsubscribe();
    this.unSubscribeEvaluationUpdates();
  }

  public getEvaluationLogDetails(loading: boolean): void {
    this.loading = loading;
    this.classifiersService.getEvaluationLogDetails(this.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (evaluationLogDetails: EvaluationLogDetailsDto) => {
          this.evaluationLogDetails = evaluationLogDetails;
          if (!this.finalRequestStatuses.includes(this.evaluationLogDetails.requestStatus.value)) {
            this.subscribeForEvaluationUpdate();
          }
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

  public isRequestStatusBlink(field: string, requestStatus: string): boolean {
    return this.blink && field == 'requestStatus.description' && this.evaluationLogDetails
      && this.evaluationLogDetails.requestStatus.value == requestStatus;
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

  private handleEvaluationStatusPush(pushRequestDto: PushRequestDto): void {
    const requestStatus = pushRequestDto.additionalProperties[PushVariables.EVALUATION_REQUEST_STATUS];
    if (this.finalRequestStatuses.includes(requestStatus)) {
      this.unSubscribeEvaluationUpdates();
    }
  }

  private subscribeForEvaluationUpdate(): void {
    if (!this.evaluationUpdatesSubscription) {
      Logger.debug(`Subscribe evaluation ${this.evaluationLogDetails.requestId} status change`);
      const filterPredicate = (pushRequestDto: PushRequestDto) => {
        if (pushRequestDto.pushType == 'USER_NOTIFICATION' && pushRequestDto.messageType == PushMessageType.EVALUATION_STATUS_CHANGE) {
          const id = pushRequestDto.additionalProperties[PushVariables.EVALUATION_ID];
          return this.evaluationLogDetails.id == Number(id);
        }
        return false;
      };
      this.evaluationUpdatesSubscription = this.pushService.pushMessageSubscribe(filterPredicate)
        .subscribe({
          next: (pushRequestDto: PushRequestDto) => {
            this.handleEvaluationStatusPush(pushRequestDto);
            this.blink = true;
            this.getEvaluationLogDetails(false);
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
          }
        });
    }
  }

  private subscribeForRouteChanges(): void {
    //Subscribe for route changes in current details component
    //Used for route from evaluation log details to another evaluation log details via push
    this.routeUpdateSubscription = this.router.events.pipe(
      filter((event: RouterEvent) => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.id = this.route.snapshot.params.id;
      this.blink = false;
      this.getEvaluationLogDetails(true);
    });
  }

  private unSubscribeEvaluationUpdates(): void {
    if (this.evaluationUpdatesSubscription) {
      this.evaluationUpdatesSubscription.unsubscribe();
      this.evaluationUpdatesSubscription = null;
      Logger.debug(`Unsubscribe evaluation ${this.evaluationLogDetails.requestId} status change`);
    }
  }

  private initEvaluationLogFields(): void {
    this.evaluationLogFields = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки:" },
      { name: EvaluationLogFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки:" },
      { name: EvaluationLogFields.CREATED_BY, label: "Пользователь" },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка:" },
      { name: EvaluationLogFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: EvaluationLogFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: EvaluationLogFields.NUM_CLASSES, label: "Число классов:" },
      { name: EvaluationLogFields.CLASS_NAME, label: "Атрибут класса:" },
      { name: EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности:" },
      { name: EvaluationLogFields.PCT_CORRECT, label: "Точность классификатора, %:" },
      { name: EvaluationLogFields.EVALUATION_TOTAL_TIME, label: "Время построения модели:" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки:" },
      { name: EvaluationLogFields.START_DATE, label: "Дата начала обработки заявки:" },
      { name: EvaluationLogFields.END_DATE, label: "Дата окончания обработки заявки:" },
      { name: EvaluationLogFields.MODEL_PATH, label: "Модель классификатора:" },
      { name: EvaluationLogFields.DELETED_DATE, label: "Дата удаления модели:" },
    ];
  }
}
