import { Component, Injector, OnDestroy } from '@angular/core';
import {
  CreateEvaluationResponseDto,
  EvaluationLogDto,
  FilterDictionaryDto,
  FilterDictionaryValueDto,
  FilterFieldDto, FormTemplateDto,
  FormTemplateGroupDto,
  PageDto,
  PageRequestDto, PushRequestDto,
  RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifiersService } from "../services/classifiers.service";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { EvaluationLogFields } from "../../common/util/field-names";
import { ReportsService } from "../../common/services/report.service";
import { ReportType } from "../../common/model/report-type.enum";
import { InstancesInfoService } from "../../common/instances-info/services/instances-info.service";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MenuItem, MessageService } from "primeng/api";
import { FieldService } from "../../common/services/field.service";
import { InstancesInfoFilterValueTransformer } from "../../filter/autocomplete/transformer/instances-info-filter-value-transformer";
import { InstancesInfoAutocompleteHandler } from "../../filter/autocomplete/handler/instances-info-autocomplete-handler";
import { EvaluationLogFilterFields } from "../../common/util/filter-field-names";
import { FormTemplatesService } from "../../form-templates/services/form-templates.service";
import { FormTemplatesMapper } from "../../form-templates/services/form-templates.mapper";
import { ClassifierGroupTemplatesType } from "../../form-templates/model/classifier-group-templates.type";
import { FormField } from "../../form-templates/model/form-template.model";
import { EvaluationRequest } from "../../create-classifier/model/evaluation-request.model";
import { CreateEvaluationRequestDto } from "../../create-classifier/model/create-evaluation-request.model";
import { Logger } from "../../common/util/logging";
import { finalize } from "rxjs/internal/operators";
import { ErrorHandler } from "../../common/services/error-handler";
import { ValidationErrorCode } from "../../common/model/validation-error-code";
import { Subscription } from "rxjs";
import { PushMessageType } from "../../common/util/push-message.type";
import { PushVariables } from "../../common/util/push-variables";
import { PushService } from "../../common/push/push.service";
import { OptimalEvaluationRequest } from "../../create-optimal-classifier/model/optimal-evaluation-request.model";
import {
  CreateOptimalEvaluationRequestDto
} from "../../create-optimal-classifier/model/create-optimal-evaluation-request.model";
import { CurrentUserFilterService } from "../../filter/services/current-user-filter-service";
import { UserInfoAutocompleteHandler } from "../../filter/autocomplete/handler/user-info-autocomplete-handler";
import { UserInfoFilterValueTransformer } from "../../filter/autocomplete/transformer/user-info-filter-value-transformer";
import { UsersService } from "../../users/services/users.service";
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> implements OnDestroy {

  private static readonly EVALUATION_LOGS_REPORT_FILE_NAME = 'evaluation-logs-report.xlsx';

  private readonly errorCodes: string[] = [
    ValidationErrorCode.CLASS_ATTRIBUTE_NOT_SELECTED,
    ValidationErrorCode.INSTANCES_NOT_FOUND,
    ValidationErrorCode.SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW,
    ValidationErrorCode.CLASS_VALUES_IS_TOO_LOW,
    ValidationErrorCode.CLASSIFIER_OPTIONS_NOT_FOUND
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.CLASS_ATTRIBUTE_NOT_SELECTED, 'Не выбран атрибут класса для заданной обучающей выборки')
    .set(ValidationErrorCode.INSTANCES_NOT_FOUND, 'Обучающая выборка не найдена')
    .set(ValidationErrorCode.SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW, 'Выберите не менее двух атрибутов классификации')
    .set(ValidationErrorCode.CLASS_VALUES_IS_TOO_LOW, 'Число классов должно быть не менее двух')
    .set(ValidationErrorCode.CLASSIFIER_OPTIONS_NOT_FOUND, 'Оптимальные настройки классификатора не найдены для заданной обучающей выборки и метода оценки точности')

  private currentUserFilterService: CurrentUserFilterService;

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public classifierTemplateGroups: FormTemplateGroupDto[] = [];

  public classifierOptionsMenu: MenuItem[] = [];

  public selectedClassifierTemplate: FormTemplateDto;
  public selectedClassifierFormFields: FormField[] = [];
  public evaluationRequest: EvaluationRequest = new EvaluationRequest();
  public optimalEvaluationRequest: OptimalEvaluationRequest = new OptimalEvaluationRequest();

  public createClassifierDialogVisibility: boolean = false;

  public createOptimalClassifierDialogVisibility: boolean = false;

  private evaluationUpdatesSubscriptions: Subscription;

  public constructor(private injector: Injector,
                     private classifiersService: ClassifiersService,
                     private filterService: FilterService,
                     private reportsService: ReportsService,
                     private instancesInfoService: InstancesInfoService,
                     private formTemplatesService: FormTemplatesService,
                     private formTemplatesMapper: FormTemplatesMapper,
                     private errorHandler: ErrorHandler,
                     private pushService: PushService,
                     private usersService: UsersService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = EvaluationLogFields.CREATION_DATE;
    this.linkColumns = [EvaluationLogFields.CLASSIFIER_DESCRIPTION, EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION,
      EvaluationLogFields.RELATION_NAME, EvaluationLogFields.REQUEST_ID, EvaluationLogFields.MODEL_PATH];
    this.notSortableColumns = [EvaluationLogFields.EVALUATION_TOTAL_TIME, EvaluationLogFields.MODEL_PATH,
      EvaluationLogFields.START_DATE, EvaluationLogFields.END_DATE, EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION,
      EvaluationLogFields.REQUEST_STATUS_DESCRIPTION, EvaluationLogFields.DELETED_DATE
    ];
    this.initColumns();
  }

  public ngOnInit() {
    this.addLazyReferenceTransformers(new InstancesInfoFilterValueTransformer());
    this.addLazyReferenceTransformers(new UserInfoFilterValueTransformer(EvaluationLogFilterFields.CREATED_BY));
    this.addAutoCompleteHandler(new InstancesInfoAutocompleteHandler(this.instancesInfoService, this.messageService));
    this.addAutoCompleteHandler(new InstancesInfoAutocompleteHandler(this.instancesInfoService, this.messageService));
    this.addAutoCompleteHandler(new UserInfoAutocompleteHandler(EvaluationLogFilterFields.CREATED_BY, this.usersService, this.messageService));
    this.currentUserFilterService = new CurrentUserFilterService(EvaluationLogFilterFields.CREATED_BY, this.usersService, this.messageService);
    this.getFilterFields();
    this.getRequestStatusesStatistics();
    this.getEvaluationMethods();
    this.getClassifierTemplateGroups();
    this.subscribeForCurrentUserEvaluationUpdates();
  }

  public ngOnDestroy(): void {
    if (this.evaluationUpdatesSubscriptions) {
      this.evaluationUpdatesSubscriptions.unsubscribe();
    }
  }

  public getFilterFields() {
    this.filterService.getEvaluationLogFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    return this.classifiersService.getEvaluations(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.classifiersService.getRequestStatusesStatistics()
      .subscribe({
        next: (requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
          this.requestStatusStatisticsDto = requestStatusStatisticsDto;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public generateReport() {
    const observable = this.reportsService.getBaseReport(this.pageRequestDto, ReportType.EVALUATION_LOGS);
    this.downloadReport(observable, ClassifierListComponent.EVALUATION_LOGS_REPORT_FILE_NAME);
  }

  public onSelect(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    switch (column) {
      case EvaluationLogFields.REQUEST_ID:
        this.router.navigate([RouterPaths.EVALUATION_DETAILS_URL, evaluationLog.id]);
        break;
      case EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION:
        if (evaluationLog.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
          this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
        }
        break;
      case EvaluationLogFields.MODEL_PATH:
        this.downloadModel(evaluationLog);
        break;
      default:
        this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
    }
  }

  public downloadModel(evaluationLogDto: EvaluationLogDto): void {
    this.loading = true;
    this.classifiersService.downloadModel(evaluationLogDto,
      () => this.loading = false,
      (error) => this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message })
    );
  }

  public loadCurrentUserToFilter(): void {
    this.currentUserFilterService.getCurrentUser(this.filters);
  }

  private toggleOverlayPanel(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getEvaluationMethods(): void {
    this.filterService.getEvaluationMethodDictionary()
      .subscribe({
        next: (filterDictionary: FilterDictionaryDto) => {
          this.evaluationMethods = filterDictionary.values;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public onCreateClassifierDialogVisibility(visible): void {
    this.createClassifierDialogVisibility = visible;
  }

  public onCreateOptimalClassifierDialogVisibility(visible): void {
    this.createOptimalClassifierDialogVisibility = visible;
  }

  public onCreateClassifier(evaluationRequest: EvaluationRequest): void {
    const classifierOptions = this.formTemplatesMapper.mapToClassifierOptionsObject(evaluationRequest.classifierOptions,
      this.selectedClassifierTemplate);
    if (environment.debug) {
      console.log(`Create evaluation request classifier options: ${JSON.stringify(classifierOptions)}`);
    }
    const createEvaluationRequest =
      new CreateEvaluationRequestDto(evaluationRequest.instancesUuid, classifierOptions, evaluationRequest.evaluationMethod.value);
    this.loading = true;
    this.classifiersService.createEvaluationRequest(createEvaluationRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (createEvaluationResponseDto: CreateEvaluationResponseDto) => {
          this.handleEvaluationRequestCreated(createEvaluationResponseDto);
        },
        error: (error) => {
          this.handleCreateEvaluationRequestError(error);
        }
      });
  }

  public onCreateOptimalClassifier(evaluationRequest: OptimalEvaluationRequest): void {
    const createEvaluationRequest =
      new CreateOptimalEvaluationRequestDto(evaluationRequest.instancesUuid, evaluationRequest.evaluationMethod.value);
    this.loading = true;
    this.classifiersService.createOptimalEvaluationRequest(createEvaluationRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (createEvaluationResponseDto: CreateEvaluationResponseDto) => {
          this.handleEvaluationRequestCreated(createEvaluationResponseDto);
        },
        error: (error) => {
          this.handleCreateEvaluationRequestError(error);
        }
      });
  }

  private handleCreateEvaluationRequestError(error): void {
    const errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
    if (errorCode) {
      const errorMessage = this.errorCodesMap.get(errorCode);
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: errorMessage });
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error });
    }
  }

  private handleEvaluationRequestCreated(createEvaluationResponseDto: CreateEvaluationResponseDto): void {
    Logger.debug(`Evaluation request ${createEvaluationResponseDto.requestId} has been created`);
    if (!this.lastCreatedId) {
      this.lastCreatedId = createEvaluationResponseDto.id;
      this.getRequestStatusesStatistics();
      this.reloadPageWithLoader();
    }
  }

  private subscribeForCurrentUserEvaluationUpdates(): void {
    const filterPredicate = (pushRequestDto: PushRequestDto) =>
      pushRequestDto.pushType == 'USER_NOTIFICATION' && pushRequestDto.messageType == PushMessageType.EVALUATION_STATUS_CHANGE;
    this.evaluationUpdatesSubscriptions = this.pushService.pushMessageSubscribe(filterPredicate)
      .subscribe({
        next: (pushRequestDto: PushRequestDto) => {
          if (!this.lastCreatedId) {
            this.lastCreatedId = pushRequestDto.additionalProperties[PushVariables.EVALUATION_ID];
            this.reloadDataQuietly();
          }
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private reloadDataQuietly(): void {
    this.reloadPage(false);
    this.getRequestStatusesStatistics();
  }

  private getClassifierTemplateGroups(): void {
    this.formTemplatesService.getClassifiersFormTemplates(ClassifierGroupTemplatesType.ALL)
      .subscribe({
        next: (templateGroups: FormTemplateGroupDto[]) => {
          this.classifierTemplateGroups = templateGroups;
          this.initClassifiersMenu();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initClassifiersMenu(): void {
    if (this.classifierTemplateGroups) {
      const items: MenuItem[] = [];
      this.classifierTemplateGroups.forEach((templatesGroup: FormTemplateGroupDto) => {
        const groupItems: MenuItem[] = this.initClassifierGroupTemplates(templatesGroup);
        const groupItem = {
          label: templatesGroup.groupTitle,
          styleClass: 'menu-item',
          items: groupItems
        };
        items.push(groupItem);
      });
      this.classifierOptionsMenu = [
        {
          label: 'Новый классификатор',
          icon: 'pi pi-plus',
          styleClass: 'main-menu-item',
          items: items
        },
        {
          label: 'Оптимальный классификатор',
          icon: 'pi pi-plus',
          styleClass: 'main-menu-item',
          command: () => {
            this.optimalEvaluationRequest = new OptimalEvaluationRequest();
            this.createOptimalClassifierDialogVisibility = true;
          }
        },
        {
          label: 'Сформировать отчет',
          icon: 'pi pi-file',
          styleClass: 'main-menu-item',
          command: () => {
            this.generateReport();
          }
        }
      ];
    }
  }

  private initClassifierGroupTemplates(templatesGroup: FormTemplateGroupDto): MenuItem[] {
    return templatesGroup.templates.map((template: FormTemplateDto) => {
      return {
        label: template.templateTitle,
        styleClass: 'classifier-menu-item',
        command: () => {
          this.selectedClassifierTemplate = template;
          this.selectedClassifierFormFields = this.formTemplatesMapper.mapToFormFields(template.fields);
          this.evaluationRequest = new EvaluationRequest();
          this.evaluationRequest.classifierOptions = this.selectedClassifierFormFields;
          this.createClassifierDialogVisibility = true;
        }
      };
    });
  }

  private initColumns() {
    this.columns = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки", sortBy: EvaluationLogFilterFields.REQUEST_ID },
      { name: EvaluationLogFields.CLASSIFIER_DESCRIPTION, label: "Классификатор", sortBy: EvaluationLogFilterFields.CLASSIFIER_NAME },
      { name: EvaluationLogFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки"  },
      { name: EvaluationLogFields.PCT_CORRECT, label: "Точность классификатора, %", sortBy: EvaluationLogFilterFields.PCT_CORRECT },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка", sortBy: EvaluationLogFilterFields.RELATION_NAME },
      { name: EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности"  },
      { name: EvaluationLogFields.CREATED_BY, label: "Пользователь", sortBy: EvaluationLogFilterFields.CREATED_BY },
      { name: EvaluationLogFields.MODEL_PATH, label: "Модель классификатора" },
      { name: EvaluationLogFields.EVALUATION_TOTAL_TIME, label: "Время построения модели" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки", sortBy: EvaluationLogFilterFields.CREATION_DATE },
      { name: EvaluationLogFields.START_DATE, label: "Дата начала обработки заявки"  },
      { name: EvaluationLogFields.END_DATE, label: "Дата окончания обработки заявки"  },
      { name: EvaluationLogFields.DELETED_DATE, label: "Дата удаления модели" }
    ];
  }
}
