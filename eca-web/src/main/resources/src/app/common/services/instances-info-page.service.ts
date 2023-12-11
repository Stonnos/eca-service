import {
  InstancesInfoDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

export interface InstancesInfoPageService {

  getInstancesInfoPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesInfoDto>>;
}
