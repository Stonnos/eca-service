import { Component, Input, OnInit } from '@angular/core';
import { AttributeStatisticsDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AttributeStatisticsFields } from "../../../common/util/field-names";
import { FieldService } from "../../../common/services/field.service";

@Component({
  selector: 'app-numeric-attribute-statistics-table',
  templateUrl: './numeric-attribute-statistics-table.component.html',
  styleUrls: ['./numeric-attribute-statistics-table.component.scss']
})
export class NumericAttributeStatisticsTableComponent implements OnInit {

  public statsMetaData: any[] = [];

  @Input()
  public attributeStatisticsDto: AttributeStatisticsDto;

  public constructor(private fieldService: FieldService) {
    this.initStatisticsMetaData();
  }

  public ngOnInit() {
  }

  public getValues() {
    if (!this.attributeStatisticsDto) {
      return [];
    } else {
      return this.statsMetaData.map((meta: any) => {
        return {
          statisticsLabel: meta.label,
          value: this.fieldService.getFieldValue(meta.name, this.attributeStatisticsDto, null)
        };
      });
    }
  }

  private initStatisticsMetaData() {
    this.statsMetaData = [
      { name: AttributeStatisticsFields.MIN_VALUE, label: "Минимальное значение" },
      { name: AttributeStatisticsFields.MAX_VALUE, label: "Максимальное значение" },
      { name: AttributeStatisticsFields.MEAN_VALUE, label: "Математическое ожидание" },
      { name: AttributeStatisticsFields.VARIANCE_VALUE, label: "Дисперсия" },
      { name: AttributeStatisticsFields.STD_DEV_VALUE, label: "Среднеквадратическое отклонение" },
    ];
  }
}
