import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto,
  ExperimentErsReportDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { saveAs } from 'file-saver/dist/FileSaver';
import { ExperimentFields } from "../../common/util/field-names";

@Component({
  selector: 'app-experiment-details',
  templateUrl: './experiment-details.component.html',
  styleUrls: ['./experiment-details.component.scss']
})
export class ExperimentDetailsComponent implements OnInit {

  private readonly experimentUuid: string;

  public experimentFields: any[] = [];

  public experimentDto: ExperimentDto;

  public experimentErsReport: ExperimentErsReportDto;

  public linkColumns: string[] = [ExperimentFields.TRAINING_DATA_PATH, ExperimentFields.EXPERIMENT_PATH];

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute) {
    this.experimentUuid = this.route.snapshot.params.id;
    this.initExperimentFields();
  }

  public ngOnInit(): void {
    this.getExperiment();
    this.getExperimentErsReport();
  }

  public getExperiment(): void {
    this.experimentsService.getExperiment(this.experimentUuid)
      .subscribe((experimentDto: ExperimentDto) => {
        this.experimentDto = experimentDto;
      }, (error) => {
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
      });
  }

  public getExperimentTrainingDataFile(): void {
    this.experimentsService.getExperimentTrainingDataFile(this.experimentDto.uuid)
      .subscribe((blob: Blob) => {
        saveAs(blob, this.experimentDto.trainingDataAbsolutePath);
      }, (error) => {
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
      });
  }

  public getExperimentResultsFile(): void {
    this.experimentsService.getExperimentResultsFile(this.experimentDto.uuid)
      .subscribe((blob: Blob) => {
        saveAs(blob, this.experimentDto.experimentAbsolutePath);
      }, (error) => {
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
      });
  }

  public getExperimentErsReport(): void {
    this.experimentsService.getExperimentErsReport(this.experimentUuid)
      .subscribe((experimentErsReport: ExperimentErsReportDto) => {
        this.experimentErsReport = experimentErsReport;
      }, (error) => {
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
      });
  }

  public isLink(field: string): boolean {
    return this.linkColumns.includes(field);
  }

  public onLink(field: string) {
    switch (field) {
      case ExperimentFields.TRAINING_DATA_PATH:
        this.getExperimentTrainingDataFile();
        break;
      case ExperimentFields.EXPERIMENT_PATH:
        this.getExperimentResultsFile();
        break;
      default:
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${field} as link`});
    }
  }

  public getExperimentValue(field: string) {
    if (this.experimentDto) {
      switch (field) {
        case ExperimentFields.EVALUATION_METHOD:
          return this.experimentDto.evaluationMethod.description;
        case ExperimentFields.EXPERIMENT_STATUS:
          return this.experimentDto.experimentStatus.description;
        case ExperimentFields.EXPERIMENT_TYPE:
          return this.experimentDto.experimentType.description;
        default:
          return this.experimentDto[field];
      }
    }
    return null;
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: ExperimentFields.UUID, label: "UUID заявки" },
      { name: ExperimentFields.EXPERIMENT_STATUS, label: "Статус заявки" },
      { name: ExperimentFields.EVALUATION_METHOD, label: "Метод оценки точности" },
      { name: ExperimentFields.EMAIL, label: "Email заявки" },
      { name: ExperimentFields.CREATION_DATE, label: "Дата создания заявки" },
      { name: ExperimentFields.START_DATE, label: "Дата начала эксперимента" },
      { name: ExperimentFields.END_DATE, label: "Дата окончания эксперимента" },
      { name: ExperimentFields.SENT_DATE, label: "Дата отправки результатов" },
      { name: ExperimentFields.DELETED_DATE, label: "Дата удаления результатов" },
      { name: ExperimentFields.TRAINING_DATA_PATH, label: "Обучающая выборка" },
      { name: ExperimentFields.EXPERIMENT_PATH, label: "Результаты эксперимента" }
    ];
  }
}
