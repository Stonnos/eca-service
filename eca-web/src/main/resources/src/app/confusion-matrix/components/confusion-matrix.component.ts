import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {
  ConfusionMatrixDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-confusion-matrix-list',
  templateUrl: './confusion-matrix.component.html',
  styleUrls: ['./confusion-matrix.component.scss']
})
export class ConfusionMatrixComponent implements OnInit, OnChanges {

  public columns: any[] = [];
  public caption: string = 'Матрица классификации';

  @Input()
  public confusionMatrix: ConfusionMatrixDto;

  public constructor() {
  }

  public ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    this.initColumns();
  }

  private initColumns(): void {
    if (this.confusionMatrix) {
      this.columns = [
        {
          name: "#",
          label: "Реальное"
        }
      ];
      this.confusionMatrix.classValues.forEach((classValue: string) => {
        this.columns.push({
          name: classValue,
          label: `${classValue} (Прогнозное)`
        });
      });
    }
  }
}
