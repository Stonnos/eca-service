import { Component, Input, OnInit } from '@angular/core';
import {
  ClassificationCostsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassificationCostsFields } from "../../common/util/field-names";

@Component({
  selector: 'app-classification-costs-list',
  templateUrl: './classification-costs.component.html',
  styleUrls: ['./classification-costs.component.scss']
})
export class ClassificationCostsComponent implements OnInit {

  public columns: any[] = [];
  public caption: string = 'Результаты классификации';

  @Input()
  public items: ClassificationCostsDto[] = [];

  public constructor() {
    this.initColumns();
  }

  public ngOnInit() {
  }

  private initColumns() {
    this.columns = [
      { name: ClassificationCostsFields.CLASS_VALUE, label: "Класс" },
      { name: ClassificationCostsFields.TPR, label: "TPR" },
      { name: ClassificationCostsFields.FPR, label: "FPR" },
      { name: ClassificationCostsFields.TNR, label: "TNR" },
      { name: ClassificationCostsFields.FNR, label: "FNR" },
      { name: ClassificationCostsFields.AUC, label: "AUC" }
    ];
  }
}
