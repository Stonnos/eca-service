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

@Injectable()
export class InstancesService {

  private serviceUrl = environment.dsUrl + '/instances';

  public constructor(private http: HttpClient) {
  }

  public getInstancesPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<InstancesDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public deleteInstances(id: number): Observable<any> {
    let params = new HttpParams().set('id', id.toString());
    const options = { params: params };
    return this.http.delete(this.serviceUrl + '/delete', options);
  }

  public saveData(file: File, tableName: string): Observable<CreateInstancesResultDto> {
    const formData = new FormData();
    formData.append('trainingData', file, file.name);
    formData.append('relationName', tableName);
    return this.http.post<CreateInstancesResultDto>(this.serviceUrl + '/save', formData);
  }

  public renameData(id: number, newTableName: string): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('relationName', newTableName);
    return this.http.put(this.serviceUrl + '/rename', formData);
  }

  public getInstancesDetails(id: number): Observable<InstancesDto> {
    return this.http.get<InstancesDto>(this.serviceUrl + '/details/' + id);
  }

  public getAttributes(id: number): Observable<AttributeDto[]> {
    return this.http.get<AttributeDto[]>(this.serviceUrl + '/attributes/' + id);
  }

  public getDataPage(id: number, pageRequest: PageRequestDto): Observable<PageDto<string[]>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    const params: HttpParams = new HttpParams()
      .set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.post<PageDto<string[]>>(this.serviceUrl + '/data-page', pageRequest, options);
  }

  public getInstancesReportsInfo(): Observable<InstancesReportInfoDto[]> {
    return this.http.get<InstancesReportInfoDto[]>(this.serviceUrl + '/reports-info');
  }

  public downloadInstancesReport(id: number, reportType: string): Observable<Blob> {
    const params: HttpParams = new HttpParams()
      .set('id', id.toString())
      .set('reportType', reportType);
    const options = { params: params, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/download', options);
  }

  public selectAllAttributes(id: number): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.put(this.serviceUrl + '/select-all-attributes', formData);
  }

  public selectAttribute(id: number): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.put(this.serviceUrl + '/select-attribute', formData);
  }

  public unselectAttribute(id: number): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.put(this.serviceUrl + '/unselect-attribute', formData);
  }

  public setClassAttribute(classAttributeId: number): Observable<any> {
    const formData = new FormData();
    formData.append('classAttributeId', classAttributeId.toString());
    return this.http.put(this.serviceUrl + '/set-class-attribute', formData);
  }

  public getInstancesStatistics(id: number): Observable<InstancesStatisticsDto> {
    return this.http.get<InstancesStatisticsDto>(this.serviceUrl + '/instances-stats/' + id);
  }

  public getAttributeStatistics(id: number): Observable<AttributeStatisticsDto> {
    return this.http.get<AttributeStatisticsDto>(this.serviceUrl + '/attribute-stats/' + id);
  }
}
