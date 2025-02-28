import {
  EnumDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from 'primeng/api';

export class AttributeMetaInfoModel {
  name: string;
  index: number;
  type: EnumDto;
  dateFormat: string;
  values: SelectItem[];
  currentValue: any;

  public constructor(name: string, index: number, type: EnumDto) {
    this.name = name;
    this.index = index;
    this.type = type;
  }
}
