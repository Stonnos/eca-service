import { Component, Input, OnInit } from '@angular/core';
import { AttributeStatisticsDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { FrequencyDiagramDataFields } from "../../../common/util/field-names";

@Component({
  selector: 'app-nominal-attribute-statistics-table',
  templateUrl: './nominal-attribute-statistics-table.component.html',
  styleUrls: ['./nominal-attribute-statistics-table.component.scss']
})
export class NominalAttributeStatisticsTableComponent implements OnInit {

  public columns: any[] = [];

  @Input()
  public attributeStatisticsDto: AttributeStatisticsDto;

  public constructor() {
    this.initColumns();
  }

  public ngOnInit() {
  }

  private initColumns() {
    this.columns = [
      { name: FrequencyDiagramDataFields.CODE, label: "Значение" },
      { name: FrequencyDiagramDataFields.FREQUENCY, label: "Число значений" },
    ];
  }
}
