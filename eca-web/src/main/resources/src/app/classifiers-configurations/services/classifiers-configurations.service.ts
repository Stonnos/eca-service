import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifiersConfigurationDto, ClassifiersConfigurationHistoryDto, CreateClassifiersConfigurationDto,
  PageDto,
  PageRequestDto, UpdateClassifiersConfigurationDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class ClassifiersConfigurationsService {

  private serviceUrl = environment.serverUrl + '/experiment/classifiers-configurations';

  public constructor(private http: HttpClient) {
  }

  public getClassifiersConfigurations(pageRequest: PageRequestDto): Observable<PageDto<ClassifiersConfigurationDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<ClassifiersConfigurationDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getClassifiersConfigurationDetails(configurationId: number): Observable<ClassifiersConfigurationDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ClassifiersConfigurationDto>(this.serviceUrl + '/details/' + configurationId.toString(), { headers: headers });
  }

  public saveConfiguration(configuration: CreateClassifiersConfigurationDto): Observable<ClassifiersConfigurationDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<ClassifiersConfigurationDto>(this.serviceUrl + '/save', configuration, { headers: headers })
  }

  public updateConfiguration(configuration: UpdateClassifiersConfigurationDto): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.put(this.serviceUrl + '/update', configuration, { headers: headers })
  }

  public copyConfiguration(configuration: UpdateClassifiersConfigurationDto): Observable<ClassifiersConfigurationDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<ClassifiersConfigurationDto>(this.serviceUrl + '/copy', configuration, { headers: headers })
  }

  public deleteConfiguration(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    let params = new HttpParams().set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.delete(this.serviceUrl + '/delete', options);
  }

  public setActive(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.post(this.serviceUrl + '/set-active', formData, { headers: headers });
  }

  public getClassifiersConfigurationReport(configurationId: number): Observable<Blob>  {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/report/' + configurationId.toString(), options);
  }

  public getClassifiersConfigurationHistory(configurationId: number, pageRequest: PageRequestDto): Observable<PageDto<ClassifiersConfigurationHistoryDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    let params = new HttpParams().set('configurationId', configurationId.toString());
    const options = { headers: headers, params: params };
    return this.http.post<PageDto<ClassifiersConfigurationHistoryDto>>(this.serviceUrl + '/history', pageRequest, options);
  }
}
