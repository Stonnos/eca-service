import {
  FilterFieldType,
  MatchMode
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";

export class Filter {
  name: string;
  label: string;
  filterFieldType: FilterFieldType;
  matchMode: MatchMode;
  multiple: boolean;
  currentValue: any;
  currentValues: any[];
  values: SelectItem[] = [];

  constructor(name: string, label: string, filterFieldType: FilterFieldType, matchMode: MatchMode, values?: SelectItem[]) {
    this.name = name;
    this.label = label;
    this.filterFieldType = filterFieldType;
    this.matchMode = matchMode;
    this.values = values;
  }
}
