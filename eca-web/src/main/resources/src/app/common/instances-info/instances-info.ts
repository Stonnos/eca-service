import { Component, Injector } from '@angular/core';
import {
  InstancesInfoDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { InstancesInfoDtoFields } from "../util/field-names";
import { FieldService } from "../services/field.service";
import { BaseDetailsComponent } from "../base-details/base-details.component";

@Component({
  selector: 'app-instances-info',
  templateUrl: './instances-info.html',
  styleUrls: ['./instances-info.scss']
})
export class InstancesInfo extends BaseDetailsComponent<InstancesInfoDto> {

  public constructor(private injector: Injector) {
    super(injector.get(FieldService));
  }

  public ngOnInit(): void {
    this.fields = [
      { name: InstancesInfoDtoFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: InstancesInfoDtoFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: InstancesInfoDtoFields.NUM_CLASSES, label: "Число классов:" },
      { name: InstancesInfoDtoFields.CLASS_NAME, label: "Атрибут класса:" },
    ];
  }
}
