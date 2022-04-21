import {
  FieldType
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";

export class FormField {
  name: string;
  label: string;
  fieldType: FieldType;
  maxLength: number;
  min: number;
  max: number;
  pattern: string;
  defaultValue: string;
  currentValue: any;
  values: SelectItem[] = [];

  constructor(name: string, label: string, fieldType: FieldType) {
    this.name = name;
    this.label = label;
    this.fieldType = fieldType;
  }
}