import {
  RocCurveDataDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

export interface RocCurveService {

  getRocCurveData(modelId: number, classValueIndex: number): Observable<RocCurveDataDto>;
}
