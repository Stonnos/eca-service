import { ContingencyTableRequestDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

export class ContingencyTableRequestModel implements ContingencyTableRequestDto {
  instancesId: number;
  xattributeId: number;
  yattributeId: number;
  alphaValue: number;
  useYates: boolean;

  public constructor(instancesId: number, xattributeId: number, yattributeId: number, alphaValue: number, useYates: boolean) {
    this.instancesId = instancesId;
    this.xattributeId = xattributeId;
    this.yattributeId = yattributeId;
    this.alphaValue = alphaValue;
    this.useYates = useYates;
  }
}
