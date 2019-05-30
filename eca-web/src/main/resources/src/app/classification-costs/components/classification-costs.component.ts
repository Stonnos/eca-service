import { Component, Input, OnInit } from '@angular/core';
import {
  ClassificationCostsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

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
      { name: "classValue", label: "Класс" },
      { name: "truePositiveRate", label: "TPR" },
      { name: "falsePositiveRate", label: "FPR" },
      { name: "trueNegativeRate", label: "TNR" },
      { name: "falseNegativeRate", label: "FNR" },
      { name: "aucValue", label: "AUC" }
    ];
  }
}
