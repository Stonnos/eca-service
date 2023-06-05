import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AttributeDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AttributeFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { EditAttributeModel } from "../model/edit-attribute.model";

@Component({
  selector: 'app-attributes',
  templateUrl: './attributes.component.html',
  styleUrls: ['./attributes.component.scss']
})
export class AttributesComponent implements OnInit {

  public columns: any[] = [];
  public header: string = 'Атрибуты';
  public linkColumns: string[] = [];

  @Input()
  public loading: boolean = false;
  @Input()
  public attributes: AttributeDto[] = [];
  @Input()
  public classAttribute: AttributeDto;

  @Output()
  public onClassChange: EventEmitter<AttributeDto> = new EventEmitter<AttributeDto>();
  @Output()
  public attributeSelected: EventEmitter<EditAttributeModel> = new EventEmitter<EditAttributeModel>();
  @Output()
  public selectAll: EventEmitter<any> = new EventEmitter<any>();

  public constructor(private fieldService: FieldService) {
    this.initColumns();
    this.linkColumns = [AttributeFields.NAME];
  }

  public ngOnInit() {
  }

  private initColumns() {
    this.columns = [
      { name: AttributeFields.NAME, label: "Название" },
      { name: AttributeFields.TYPE, label: "Тип" }
    ];
  }

  public getColumnValue(column: string, item: AttributeDto) {
    return this.fieldService.getFieldValue(column, item);
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  public selectAttribute(attribute: AttributeDto, event): void {
    console.log(attribute.name);
    console.log(event);
    this.attributeSelected.emit(new EditAttributeModel(attribute.id, event));
  }

  public selectAllAttributes(): void {
    this.selectAll.emit();
  }

  public setClass(event): void {
    this.onClassChange.emit(event.value);
  }
}
