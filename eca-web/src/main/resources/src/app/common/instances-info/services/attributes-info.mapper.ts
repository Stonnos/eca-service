import { Injectable } from '@angular/core';
import {
  AttributeMetaInfoDto,
  AttributeValueMetaInfoDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AttributeMetaInfoModel } from '../../../classify-instance/model/attribute-meta-info.model';
import { SelectItem } from 'primeng/api';

@Injectable()
export class AttributesInfoMapper {

  public mapAttributes(attributeMetaInfoList: AttributeMetaInfoDto[]): AttributeMetaInfoModel[] {
    return attributeMetaInfoList.map((attributeMetaInfoDto: AttributeMetaInfoDto) => {
      let attributeMetaInfoModel = new AttributeMetaInfoModel(attributeMetaInfoDto.name, attributeMetaInfoDto.index, attributeMetaInfoDto.type);
      if (attributeMetaInfoDto.type.value == 'DATE') {
        attributeMetaInfoModel.dateFormat = attributeMetaInfoDto.dateFormat;
      }
      if (attributeMetaInfoDto.type.value == 'NOMINAL') {
        attributeMetaInfoModel.values = this.mapValues(attributeMetaInfoDto.values);
      }
      return attributeMetaInfoModel;
    });
  }

  public mapValues(classValues: AttributeValueMetaInfoDto[]): SelectItem[] {
    return classValues.map((attributeValueMetaInfoDto: AttributeValueMetaInfoDto) => {
      return { label: attributeValueMetaInfoDto.value, value: attributeValueMetaInfoDto.index};
    });
  }
}
