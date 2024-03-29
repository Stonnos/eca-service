import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsDto, CreateClassifierOptionsResultDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";
import { Logger } from "../../common/util/logging";

@Injectable()
export class ClassifierOptionsService {

  private serviceUrl = environment.serverUrl + '/experiment/classifiers-options';

  public constructor(private http: HttpClient) {
  }

  public getClassifiersOptions(configurationId: number, pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = new HttpParams()
      .set('configurationId', configurationId.toString());
    const options = { headers: headers, params: params };
    return this.http.post<PageDto<ClassifierOptionsDto>>(this.serviceUrl + '/page', pageRequest, options);
  }

  public addClassifiersOptions(configurationId: number, classifierOptions: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = new HttpParams()
      .set('configurationId', configurationId.toString());
    const options = { headers: headers, params: params };
    Logger.debug(`Adds classifier options ${JSON.stringify(classifierOptions)} for configuration ${configurationId}`);
    return this.http.post(this.serviceUrl + '/add', classifierOptions, options);
  }

  public uploadClassifierOptions(configurationId: number, file: File): Observable<CreateClassifierOptionsResultDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('classifiersOptionsFile', file, file.name);
    formData.append('configurationId', configurationId.toString());
    return this.http.post<CreateClassifierOptionsResultDto>(this.serviceUrl + '/upload', formData, { headers: headers });
  }

  public deleteClassifierOptions(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    let params = new HttpParams().set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.delete(this.serviceUrl + '/delete', options);
  }
}
