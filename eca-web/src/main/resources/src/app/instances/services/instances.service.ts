import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AttributeDto, AttributeStatisticsDto,
  CreateInstancesResultDto,
  InstancesDto, InstancesReportInfoDto, InstancesStatisticsDto,
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
    formData.append('relationName', tableName);
    return this.http.post<CreateInstancesResultDto>(this.serviceUrl + '/save', formData, { headers: headers });
  }

  public renameData(id: number, newTableName: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('relationName', newTableName);
    return this.http.put(this.serviceUrl + '/rename', formData, { headers: headers });
  }

  public getInstancesDetails(id: number): Observable<InstancesDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<InstancesDto>(this.serviceUrl + '/details/' + id, { headers: headers });
  }

  public getAttributes(id: number): Observable<AttributeDto[]> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<AttributeDto[]>(this.serviceUrl + '/attributes/' + id, { headers: headers });
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

  public downloadInstancesReport(id: number, reportType: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = new HttpParams()
      .set('id', id.toString())
      .set('reportType', reportType);
    const options = { headers: headers, params: params, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/download', options);
  }

  public selectAllAttributes(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.put(this.serviceUrl + '/select-all-attributes', formData, { headers: headers });
  }

  public selectAttribute(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.put(this.serviceUrl + '/select-attribute', formData, { headers: headers });
  }

  public unselectAttribute(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.put(this.serviceUrl + '/unselect-attribute', formData, { headers: headers });
  }

  public setClassAttribute(classAttributeId: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('classAttributeId', classAttributeId.toString());
    return this.http.put(this.serviceUrl + '/set-class-attribute', formData, { headers: headers });
  }

  public getInstancesStatistics(id: number): Observable<InstancesStatisticsDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<InstancesStatisticsDto>(this.serviceUrl + '/instances-stats/' + id, { headers: headers });
  }

  public getAttributeStatistics(id: number): Observable<AttributeStatisticsDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<AttributeStatisticsDto>(this.serviceUrl + '/attribute-stats/' + id, { headers: headers });
  }
}
