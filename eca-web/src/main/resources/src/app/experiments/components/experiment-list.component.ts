import { Component, Injector, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentTypeDto, PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { MessageService, SelectItem } from "primeng/api";
import { Filter } from "../../filter/filter.model";
import { saveAs } from 'file-saver/dist/FileSaver';
import { BaseListComponent } from "../../lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent extends BaseListComponent<ExperimentDto> implements OnInit {

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public constructor(private injector: Injector,
                     private experimentsService: ExperimentsService) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = ["trainingDataAbsolutePath", "experimentAbsolutePath"];
    this.notSortableColumns = ["trainingDataAbsolutePath", "experimentAbsolutePath"];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
    this.addExperimentTypesFilter();
    this.getRequestStatusesStatistics();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    return this.experimentsService.getExperiments(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.experimentsService.getRequestStatusesStatistics().subscribe((requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
      this.requestStatusStatisticsDto = requestStatusStatisticsDto;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  public onLink(column: string, experiment: ExperimentDto) {
    switch (column) {
      case this.linkColumns[0]:
        this.loading = true;
        this.experimentsService.getExperimentTrainingDataFile(experiment.uuid).subscribe((blob: Blob) => {
          saveAs(blob, experiment.trainingDataAbsolutePath);
          this.loading = false;
        }, (error) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
          this.loading = false;
        });
        break;
      case this.linkColumns[1]:
        this.loading = true;
        this.experimentsService.getExperimentResultsFile(experiment.uuid).subscribe((blob: Blob) => {
          saveAs(blob, experiment.experimentAbsolutePath);
          this.loading = false;
        }, (error) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
          this.loading = false;
        });
        break;
    }
  }

  private initColumns() {
    this.columns = [
      { name: "uuid", label: "UUID заявки" },
      { name: "experimentType", label: "Тип эксперимента" },
      { name: "evaluationMethod", label: "Метод оценки точности" },
      { name: "firstName", label: "Имя заявки" },
      { name: "email", label: "Email заявки" },
      { name: "trainingDataAbsolutePath", label: "Обучающая выборка" },
      { name: "experimentAbsolutePath", label: "Результаты эксперимента" },
      { name: "creationDate", label: "Дата создания заявки" },
      { name: "startDate", label: "Дата начала эксперимента" },
      { name: "endDate", label: "Дата окончания эксперимента" },
      { name: "sentDate", label: "Дата отправки результатов" },
      { name: "deletedDate", label: "Дата удаления результатов" },
      { name: "experimentStatus", label: "Статус заявки" }
    ];
  }

  private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "Все", value: null },
      { label: "Использование обучающего множества", value: "TRAINING_DATA" },
      { label: "V-блочная кросс-проверка", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "Все", value: null },
      { label: "Новая", value: "NEW" },
      { label: "Завершена", value: "FINISHED" },
      { label: "Таймаут", value: "TIMEOUT" },
      { label: "Ошибка", value: "ERROR" }
    ];
    this.filters.push(new Filter("uuid", "UUID заявки",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("email", "Email заявки",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("evaluationMethod", "Метод оценки точности", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("experimentStatus", "Статус заявки", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("creationDate", "Дата создания заявки с",
      "DATE", "GTE", null));
    this.filters.push(new Filter("creationDate", "Дата создания заявки по",
      "DATE", "LTE", null));
    this.filters.push(new Filter("sentDate", "Дата отправки результатов с",
      "DATE", "GTE", null));
    this.filters.push(new Filter("sentDate", "Дата отправки результатов по",
      "DATE", "LTE", null));
  }

  private addExperimentTypesFilter() {
    this.experimentsService.getExperimentTypes().subscribe((experimentTypes: ExperimentTypeDto[]) => {
      const experimentTypeItems: SelectItem[] =
        experimentTypes.map((experimentType: ExperimentTypeDto) => {
          return { label: experimentType.description, value: experimentType.type };
        });
      experimentTypeItems.unshift({ label: "Все", value: null });
      this.filters.push(new Filter("experimentType", "Тип эксперимента", "REFERENCE", "EQUALS",
        null, experimentTypeItems));
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }
}
