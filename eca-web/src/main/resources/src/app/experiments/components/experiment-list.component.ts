import { Component, Injector, OnInit } from '@angular/core';
import {
  CreateExperimentResultDto,
  ExperimentDto, FilterDictionaryDto, FilterDictionaryValueDto, FilterFieldDto, PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { MessageService } from "primeng/api";
import { saveAs } from 'file-saver/dist/FileSaver';
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { finalize } from "rxjs/internal/operators";
import { ExperimentRequest } from "../../create-experiment/model/experiment-request.model";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router.paths";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent extends BaseListComponent<ExperimentDto> implements OnInit {

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public createExperimentDialogVisibility: boolean = false;

  public lastCreatedExperimentUuid: string;
  public blinkUuid: string;

  public experimentTypes: FilterDictionaryValueDto[] = [];
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public experimentRequest: ExperimentRequest = new ExperimentRequest();

  public constructor(private injector: Injector,
                     private experimentsService: ExperimentsService,
                     private filterService: FilterService,
                     private router: Router) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = ["trainingDataAbsolutePath", "experimentAbsolutePath", "uuid"];
    this.notSortableColumns = ["trainingDataAbsolutePath", "experimentAbsolutePath"];
    this.initColumns();
  }

  public ngOnInit() {
    this.getFilterFields();
    this.getRequestStatusesStatistics();
    this.getEvaluationMethods();
    this.getExperimentTypes();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    return this.experimentsService.getExperiments(pageRequest);
  }

  public setPage(pageDto: PageDto<ExperimentDto>) {
    this.blinkUuid = this.lastCreatedExperimentUuid;
    this.lastCreatedExperimentUuid = null;
    super.setPage(pageDto);
  }

  public getRequestStatusesStatistics() {
    this.experimentsService.getRequestStatusesStatistics().subscribe((requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
      this.requestStatusStatisticsDto = requestStatusStatisticsDto;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public getColumnValue(column: string, item: ExperimentDto) {
    switch (column) {
      case "evaluationMethod":
        return item.evaluationMethod.description;
      case "experimentStatus":
        return item.experimentStatus.description;
      case "experimentType":
        return item.experimentType.description;
      default:
        return item[column];
    }
  }

  public onLink(column: string, experiment: ExperimentDto) {
    switch (column) {
      case "trainingDataAbsolutePath":
        this.getExperimentTrainingDataFile(experiment);
        break;
      case "experimentAbsolutePath":
        this.getExperimentResultsFile(experiment);
        break;
      case "uuid":
        this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, experiment.uuid]);
        break;
      default:
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public getExperimentTrainingDataFile(experiment: ExperimentDto): void {
    this.loading = true;
    this.experimentsService.getExperimentTrainingDataFile(experiment.uuid)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((blob: Blob) => {
        saveAs(blob, experiment.trainingDataAbsolutePath);
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getExperimentResultsFile(experiment: ExperimentDto): void {
    this.loading = true;
    this.experimentsService.getExperimentResultsFile(experiment.uuid)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((blob: Blob) => {
        saveAs(blob, experiment.experimentAbsolutePath);
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public onCreateExperimentDialogVisibility(visible): void {
    this.createExperimentDialogVisibility = visible;
  }

  public onCreateExperiment(experimentRequest: ExperimentRequest): void {
    this.loading = true;
    this.experimentsService.createExperiment(experimentRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((result: CreateExperimentResultDto) => {
        if (result.created) {
          this.messageService.add({ severity: 'success', summary: `Эксперимент был успешно создан`, detail: '' });
          this.lastCreatedExperimentUuid = result.uuid;
          this.getRequestStatusesStatistics();
          this.performPageRequest(0, this.pageSize, "creationDate", false);
        } else {
          this.messageService.add({ severity: 'error', summary: 'Не удалось создать эксперимент', detail: result.errorMessage });
        }
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public isBlink(item: ExperimentDto): boolean {
    return this.blinkUuid == item.uuid;
  }

  public showCreateExperimentDialog(): void {
    this.createExperimentDialogVisibility = true;
  }

  public getFilterFields() {
    this.filterService.getExperimentFilterFields().subscribe((filterFields: FilterFieldDto[]) => {
      this.filters = this.filterService.mapToFilters(filterFields);
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public getExperimentTypes(): void {
    this.filterService.getExperimentTypeDictionary().subscribe((filterDictionary: FilterDictionaryDto) => {
      this.experimentTypes = filterDictionary.values;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public getEvaluationMethods(): void {
    this.filterService.getEvaluationMethodDictionary().subscribe((filterDictionary: FilterDictionaryDto) => {
      this.evaluationMethods = filterDictionary.values;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  private initColumns() {
    this.columns = [
      { name: "uuid", label: "UUID заявки" },
      { name: "experimentType", label: "Тип эксперимента" },
      { name: "experimentStatus", label: "Статус заявки" },
      { name: "evaluationMethod", label: "Метод оценки точности" },
      { name: "firstName", label: "Имя заявки" },
      { name: "email", label: "Email заявки" },
      { name: "trainingDataAbsolutePath", label: "Обучающая выборка" },
      { name: "experimentAbsolutePath", label: "Результаты эксперимента" },
      { name: "creationDate", label: "Дата создания заявки" },
      { name: "startDate", label: "Дата начала эксперимента" },
      { name: "endDate", label: "Дата окончания эксперимента" },
      { name: "sentDate", label: "Дата отправки результатов" },
      { name: "deletedDate", label: "Дата удаления результатов" }
    ];
  }
}
