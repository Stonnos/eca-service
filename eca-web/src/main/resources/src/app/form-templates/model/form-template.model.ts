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
  minInclusive: boolean;
  max: number;
  maxInclusive: boolean;
  pattern: string;
  defaultValue: string;
  currentValue: any;
  invalidPatternMessage: string;
  placeholder: string;
  values: SelectItem[] = [];

  constructor(name: string, label: string, fieldType: FieldType) {
    this.name = name;
    this.label = label;
    this.fieldType = fieldType;
  }
}
