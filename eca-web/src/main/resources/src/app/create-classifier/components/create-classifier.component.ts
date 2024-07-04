import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { EvaluationRequest } from "../model/evaluation-request.model";
import {
  FilterDictionaryValueDto, FormTemplateDto,
  InstancesDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { InstancesAutocompleteComponent } from "../../common/instances-autocomplete/instances-autocomplete.component";
import { FormField } from "../../form-templates/model/form-template.model";
import { FormTemplateComponent } from '../../form-templates/components/form-template.component';

@Component({
  selector: 'app-create-classifier',
  templateUrl: './create-classifier.component.html',
  styleUrls: ['./create-classifier.component.scss']
})
export class CreateClassifierComponent extends BaseCreateDialogComponent<EvaluationRequest> implements OnInit {

  @ViewChild(InstancesAutocompleteComponent, { static: true })
  private instancesAutocompleteComponent: InstancesAutocompleteComponent;

  @ViewChild(FormTemplateComponent, { static: true })
  private formTemplateComponent: FormTemplateComponent;

  @Input()
  public classifierTemplate: FormTemplateDto;

  @Input()
  public classifierOptionsFields: FormField[] = [];

  @Input()
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public ngOnInit(): void {
  }

  public clear(): void {
    this.instancesAutocompleteComponent.clear();
    super.clear();
  }

  public isValid(): boolean {
    return super.isValid() && this.formTemplateComponent.isValidAllAdditionalFields();
  }

  public selectInstances(instancesDto: InstancesDto): void {
    this.item.instancesUuid = instancesDto.uuid;
  }

  public unselectInstances(): void {
    this.item.instancesUuid = null;
  }
}
