import {
  ClassifyInstanceRequestDto, ClassifyInstanceResultDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

export interface ClassifyInstanceService {

  classifyInstance(classifyInstanceRequestDto: ClassifyInstanceRequestDto): Observable<ClassifyInstanceResultDto>;
}
