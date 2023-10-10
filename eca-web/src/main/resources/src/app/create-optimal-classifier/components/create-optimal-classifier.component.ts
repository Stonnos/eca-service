import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { OptimalEvaluationRequest } from "../model/optimal-evaluation-request.model";
import {
  FilterDictionaryValueDto,
  InstancesDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { InstancesAutocompleteComponent } from "../../common/instances-autocomplete/instances-autocomplete.component";

@Component({
  selector: 'app-create-optimal-classifier',
  templateUrl: './create-optimal-classifier.component.html',
  styleUrls: ['./create-optimal-classifier.component.scss']
})
export class CreateOptimalClassifierComponent extends BaseCreateDialogComponent<OptimalEvaluationRequest> implements OnInit {

  @ViewChild(InstancesAutocompleteComponent, { static: true })
  private instancesAutocompleteComponent: InstancesAutocompleteComponent;

  @Input()
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public ngOnInit(): void {
  }

  public clear(): void {
    this.instancesAutocompleteComponent.clear();
    super.clear();
  }

  public selectInstances(instancesDto: InstancesDto): void {
    this.item.instancesUuid = instancesDto.uuid;
  }

  public unselectInstances(): void {
    this.item.instancesUuid = null;
  }
}
