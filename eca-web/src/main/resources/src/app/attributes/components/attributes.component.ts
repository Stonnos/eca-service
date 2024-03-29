import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AttributeDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AttributeFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { EditAttributeModel } from "../model/edit-attribute.model";
import { Message } from "primeng/api";
import { OverlayPanel } from "primeng/primeng";

@Component({
  selector: 'app-attributes',
  templateUrl: './attributes.component.html',
  styleUrls: ['./attributes.component.scss']
})
export class AttributesComponent implements OnInit {

  private static readonly MIN_NUM_SELECTED_ATTRIBUTES: number = 2;

  public columns: any[] = [];
  public header: string = 'Атрибуты';
  public linkColumns: string[] = [];
  public selectedAttributesIsTooLowMessage: Message[] = [
    {
      severity: 'warn',
      detail: 'Выберите хотя бы два атрибута классификации'
    }
  ];
  public classNotSelectedMessage: Message[] = [
    {
      severity: 'warn',
      detail: 'Не выбран атрибут класса'
    }
  ];

  @Input()
  public loading: boolean = false;
  @Input()
  public attributes: AttributeDto[] = [];
  @Input()
  public classAttribute: AttributeDto;

  public toggledAttribute: AttributeDto;

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
    this.attributeSelected.emit(new EditAttributeModel(attribute.id, event));
  }

  public selectAllAttributes(): void {
    this.selectAll.emit();
  }

  public setClass(event): void {
    this.onClassChange.emit(event.value);
  }

  public selectedAttributesIsTooLow(): boolean {
    if (this.attributes.length == 0) {
      return false;
    }
    return this.attributes.filter((attr: AttributeDto) => attr.selected).length < AttributesComponent.MIN_NUM_SELECTED_ATTRIBUTES;
  }

  public isClassNotSelected(): boolean {
    return this.classAttribute === null;
  }

  public forceSetClass(classAttribute: AttributeDto): void {
    this.classAttribute = classAttribute;
  }

  public toggleOverlayPanel(event, attributeDto: AttributeDto, overlayPanel: OverlayPanel): void {
    if (attributeDto.type.value == 'NOMINAL') {
      this.toggledAttribute = attributeDto;
      overlayPanel.toggle(event);
    }
  }
}
