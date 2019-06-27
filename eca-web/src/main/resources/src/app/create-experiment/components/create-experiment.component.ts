import { Component, Input, OnInit } from '@angular/core';
import { ExperimentRequest } from "../model/experiment-request.model";
import { FilterDictionaryValueDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";

@Component({
  selector: 'app-create-experiment',
  templateUrl: './create-experiment.component.html',
  styleUrls: ['./create-experiment.component.scss']
})
export class CreateExperimentComponent extends BaseCreateDialogComponent<ExperimentRequest> implements OnInit {

  // Max file size: 10MB
  public maxFileSize: number = 10485760;
  public invalidFileSizeMessageSummary: string = '{0}: недопустимый размер файла,';
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер {0}.';
  public invalidFileTypeMessageSummary: string = '{0}: некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы форматов: {0}.';

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
    super.clear();
  }

  public isTrainingDataSelected(): boolean {
    return this.item.trainingDataFile != null;
  }

  public onUpload(event: any, fileUpload: any): void {
    console.log('On upload');
    this.item.trainingDataFile = event.files[0];
    fileUpload.clear();
  }
}
