import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentErsReportDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { ExperimentsService } from "../../experiments/services/experiments.service";

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
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getExperimentErsReport(): void {
    this.experimentsService.getExperimentErsReport(this.experimentUuid)
      .subscribe((experimentErsReport: ExperimentErsReportDto) => {
        this.experimentErsReport = experimentErsReport;
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getExperimentValue(row: string) {
    switch (row) {
      case "evaluationMethod":
        return this.experimentDto.evaluationMethod.description;
      case "experimentStatus":
        return this.experimentDto.experimentStatus.description;
      case "experimentType":
        return this.experimentDto.experimentType.description;
      default:
        return this.experimentDto[row];
    }
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: "uuid", label: "UUID заявки" },
      { name: "experimentType", label: "Тип эксперимента" },
      { name: "experimentStatus", label: "Статус заявки" },
      { name: "evaluationMethod", label: "Метод оценки точности" },
      { name: "email", label: "Email заявки" },
      { name: "trainingDataAbsolutePath", label: "Обучающая выборка" },
      { name: "experimentAbsolutePath", label: "Результаты эксперимента" }
    ];
  }
}
