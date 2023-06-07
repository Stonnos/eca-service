import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ExperimentRequest } from "../model/experiment-request.model";
import { FilterDictionaryValueDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { InstancesAutocompleteComponent } from "../../common/instances-autocomplete/instances-autocomplete.component";

@Component({
  selector: 'app-create-experiment',
  templateUrl: './create-experiment.component.html',
  styleUrls: ['./create-experiment.component.scss']
})
export class CreateExperimentComponent extends BaseCreateDialogComponent<ExperimentRequest> implements OnInit {

  @ViewChild(InstancesAutocompleteComponent, { static: true })
  private instancesAutocompleteComponent: InstancesAutocompleteComponent;

  @Input()
  public experimentTypes: FilterDictionaryValueDto[] = [];

  @Input()
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public ngOnInit(): void {
  }

  public isValid(): boolean {
    return super.isValid();
  }

  public clear(): void {
    this.instancesAutocompleteComponent.clear();
    super.clear();
  }
}
