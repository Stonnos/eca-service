import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ExperimentRequest } from "../model/experiment-request.model";
import { FilterDictionaryValueDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { UploadTrainingDataComponent } from "../../common/upload-training-data/upload-training-data.component";

@Component({
  selector: 'app-create-experiment',
  templateUrl: './create-experiment.component.html',
  styleUrls: ['./create-experiment.component.scss']
})
export class CreateExperimentComponent extends BaseCreateDialogComponent<ExperimentRequest> implements OnInit {

  @ViewChild(UploadTrainingDataComponent, { static: true })
  public fileUpload: UploadTrainingDataComponent;

  @Input()
  public experimentTypes: FilterDictionaryValueDto[] = [];

  @Input()
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public ngOnInit(): void {
  }

  public isValid(): boolean {
    return super.isValid() && this.fileUpload.isSelected();
  }

  public clear(): void {
    this.item.trainingDataFile = null;
    this.fileUpload.clearAll();
    super.clear();
  }

  public onUpload(file: File): void {
    this.item.trainingDataFile = file;
    this.fileUpload.resetUpload();
  }
}
