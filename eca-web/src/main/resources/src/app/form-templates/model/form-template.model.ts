import {
  FieldType,
  FormTemplateDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";

export class FormTemplate {
  label: string;
  template: FormTemplateDto;
  fields: FormField[];

  public constructor(label: string, template: FormTemplateDto, fields: FormField[]) {
    this.label = label;
    this.template = template;
    this.fields = fields;
  }
}

export class FormField {
  name: string;
  label: string;
  fieldType: FieldType;
  maxLength: number;
  min: number;
  minInclusive: boolean;
  max: number;
  maxInclusive: boolean;
  pattern: string;
  defaultValue: string;
  currentValue: any;
  invalidPatternMessage: string;
  placeholder: string;
  selectedItem: any;
  selectedItemToEdit: any;
  nextItemIndex: number = 0;
  editObjectItemVisibility: boolean = false;
  values: SelectItem[] = [];

  public constructor(name: string, label: string, fieldType: FieldType) {
    this.name = name;
    this.label = label;
    this.fieldType = fieldType;
  }
}

export class ObjectItem {
  index: number;
  template: FormTemplate;

  public constructor(index: number, template: FormTemplate) {
    this.index = index;
    this.template = template;
  }
}
