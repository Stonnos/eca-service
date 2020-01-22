import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ExperimentRequest } from "../model/experiment-request.model";
import { FilterDictionaryValueDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { FileUpload } from "primeng/primeng";

@Component({
  selector: 'app-create-experiment',
  templateUrl: './create-experiment.component.html',
  styleUrls: ['./create-experiment.component.scss']
})
export class CreateExperimentComponent extends BaseCreateDialogComponent<ExperimentRequest> implements OnInit {

  //Max file size: 10MB
  public maxFileSize: number = 10000000;
  //Files formats
  public accept: string = '.csv,.xls,.xlsx,.arff,.xml,.json,.txt,.data,.docx';
  public invalidFileSizeMessageSummary: string = 'Недопустимый размер файла,';
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер: {0}.';
  public invalidFileTypeMessageSummary: string = 'Некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы форматов: {0}.';

  @ViewChild(FileUpload, { static: true })
  public fileUpload: FileUpload;

  @Input()
  public experimentTypes: FilterDictionaryValueDto[] = [];

  @Input()
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public ngOnInit(): void {
  }

  public isValid(): boolean {
    return super.isValid() && this.isTrainingDataSelected();
  }

  public clear(): void {
    this.item.trainingDataFile = null;
    this.fileUpload.msgs = [];
    this.fileUpload.clear();
    super.clear();
  }

  public isTrainingDataSelected(): boolean {
    return this.item.trainingDataFile != null;
  }

  public onUpload(event: any): void {
    this.item.trainingDataFile = event.files[0];
    this.fileUpload.clear();
  }
}
