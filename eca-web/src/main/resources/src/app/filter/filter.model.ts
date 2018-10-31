import { FilterType, MatchMode } from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";

export class Filter {
  name: string;
  label: string;
  type: FilterType;
  matchMode: MatchMode;
  currentValue: string;
  values: SelectItem[] = [];

  constructor(name: string, label: string, type: FilterType, matchMode: MatchMode, currentValue: string, values?: SelectItem[]) {
    this.name = name;
    this.label = label;
    this.type = type;
    this.matchMode = matchMode;
    this.currentValue = currentValue;
    this.values = values;
  }
}
