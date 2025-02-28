export class ClassifyInstanceRequestDto {

  modelId: number;
  values: ClassifyInstanceValueDto[];

  public constructor(modelId: number, values: ClassifyInstanceValueDto[]) {
    this.modelId = modelId;
    this.values = values;
  }
}

export class ClassifyInstanceValueDto {

  index: number;
  value: number;

  public constructor(index: number, value: number) {
    this.index = index;
    this.value = value;
  }
}
