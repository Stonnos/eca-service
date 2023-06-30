import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  InstancesInfoDto, PageDto,
  PageRequestDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../../environments/environment";
import { Observable } from "rxjs/internal/Observable";
import { Utils } from "../../util/utils";

@Injectable()
export class InstancesInfoService {

  private serviceUrl = environment.serverUrl + '/instances-info';

  public constructor(private http: HttpClient) {
  }

  public getInstancesInfoPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesInfoDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<InstancesInfoDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }
}
