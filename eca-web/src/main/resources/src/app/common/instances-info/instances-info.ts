import { Component, Injector } from '@angular/core';
import {
  AbstractEvaluationDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AbstractEvaluationDtoFields } from "../util/field-names";
import { FieldService } from "../services/field.service";
import { BaseDetailsComponent } from "../base-details/base-details.component";

@Component({
  selector: 'app-instances-info',
  templateUrl: './instances-info.html',
  styleUrls: ['./instances-info.scss']
})
export class InstancesInfo extends BaseDetailsComponent<AbstractEvaluationDto> {

  public constructor(private injector: Injector) {
    super(injector.get(FieldService));
  }

  public ngOnInit(): void {
    this.fields = [
      { name: AbstractEvaluationDtoFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: AbstractEvaluationDtoFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: AbstractEvaluationDtoFields.NUM_CLASSES, label: "Число классов:" },
      { name: AbstractEvaluationDtoFields.CLASS_NAME, label: "Атрибут класса:" },
    ];
  }
}
