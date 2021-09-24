import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  CreateInstancesResultDto,
  InstancesDto, InstancesReportInfoDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class InstancesService {

  private serviceUrl = environment.dsUrl + '/instances';

  public constructor(private http: HttpClient) {
  }

  public getInstancesPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<InstancesDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
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
    formData.append('tableName', newTableName);
    return this.http.put(this.serviceUrl + '/rename', formData, { headers: headers });
  }

  public getInstancesDetails(id: number): Observable<InstancesDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<InstancesDto>(this.serviceUrl + '/details/' + id, { headers: headers });
  }

  public getAttributes(id: number): Observable<string[]> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<string[]>(this.serviceUrl + '/attributes/' + id, { headers: headers });
  }

  public getDataPage(id: number, pageRequest: PageRequestDto): Observable<PageDto<string[]>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = new HttpParams()
      .set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.post<PageDto<string[]>>(this.serviceUrl + '/data-page', pageRequest, options);
  }

  public getInstancesReportsInfo(): Observable<InstancesReportInfoDto[]> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<InstancesReportInfoDto[]>(this.serviceUrl + '/reports-info', { headers: headers });
  }
}
