import { Component, Injector, OnInit } from '@angular/core';
import { FieldService } from "../services/field.service";
import { BaseDetailsComponent } from "../base-details/base-details.component";
import { AbstractEvaluationDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AbstractEvaluationDtoFields } from "../util/field-names";

@Component({
  selector: 'app-evaluation-method-info',
  templateUrl: './evaluation-method-info.html',
  styleUrls: ['./evaluation-method-info.scss']
})
export class EvaluationMethodInfo extends BaseDetailsComponent<AbstractEvaluationDto> implements OnInit {

  public constructor(private injector: Injector) {
    super(injector.get(FieldService));
  }

  public ngOnInit(): void {
    this.fields = [
      { name: AbstractEvaluationDtoFields.NUM_FOLDS, label: "Число блоков:" },
      { name: AbstractEvaluationDtoFields.NUM_TESTS, label: "Число проверок:" },
      { name: AbstractEvaluationDtoFields.SEED, label: "Начальное значение (seed):" }
    ];
  }
}
