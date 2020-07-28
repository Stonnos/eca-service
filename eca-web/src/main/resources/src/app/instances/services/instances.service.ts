import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  CreateInstancesResultDto,
  InstancesDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { PageRequestService } from "../../common/services/page-request.service";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class InstancesService {

  private serviceUrl = environment.dsUrl + '/instances';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getInstancesPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<InstancesDto>>(this.serviceUrl + '/list', options);
  }

  public deleteInstances(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    let params = new HttpParams().set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.delete(this.serviceUrl + '/delete', options);
  }

  public saveData(file: File, tableName: string): Observable<CreateInstancesResultDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('trainingData', file, file.name);
    formData.append('tableName', tableName);
    return this.http.post<CreateInstancesResultDto>(this.serviceUrl + '/save', formData, { headers: headers });
  }

  public renameData(id: number, newTableName: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('newName', newTableName);
    return this.http.put(this.serviceUrl + '/rename', formData, { headers: headers });
  }
}
