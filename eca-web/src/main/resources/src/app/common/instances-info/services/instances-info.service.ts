import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  InstancesInfoDto, PageDto,
  PageRequestDto,
  AttributeValueMetaInfoDto,
  AttributeMetaInfoDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../../environments/environment";
import { Observable } from "rxjs/internal/Observable";
import { InstancesInfoPageService } from "../../services/instances-info-page.service";

@Injectable()
export class InstancesInfoService implements InstancesInfoPageService {

  private serviceUrl = environment.serverUrl + '/instances-info';

  public constructor(private http: HttpClient) {
  }

  public getInstancesInfoPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesInfoDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<InstancesInfoDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getClassValues(id: number): Observable<AttributeValueMetaInfoDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<AttributeValueMetaInfoDto[]>(this.serviceUrl + '/class-values/' + id, { headers: headers });
  }

  public getInputAttributes(id: number): Observable<AttributeMetaInfoDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<AttributeMetaInfoDto[]>(this.serviceUrl + '/input-attributes/' + id, { headers: headers });
  }
}
