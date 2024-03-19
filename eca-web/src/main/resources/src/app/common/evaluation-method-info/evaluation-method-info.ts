import { Component, Injector, OnInit } from '@angular/core';
import { FieldService } from "../services/field.service";
import { BaseDetailsComponent } from "../base-details/base-details.component";
import { EvaluationMethodFields } from "../util/field-names";

@Component({
  selector: 'app-evaluation-method-info',
  templateUrl: './evaluation-method-info.html',
  styleUrls: ['./evaluation-method-info.scss']
})
export class EvaluationMethodInfo extends BaseDetailsComponent<any> implements OnInit {

  public constructor(private injector: Injector) {
    super(injector.get(FieldService));
  }

  public ngOnInit(): void {
    this.fields = [
      { name: EvaluationMethodFields.NUM_FOLDS, label: "Число блоков:" },
      { name: EvaluationMethodFields.NUM_TESTS, label: "Число проверок:" },
      { name: EvaluationMethodFields.SEED, label: "Начальное значение (seed):" }
    ];
  }
}
