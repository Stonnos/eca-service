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
    return this.http.post<PageDto<ClassifierOptionsDto>>(this.serviceUrl + '/page', pageRequest, { headers: headers });
  }

  public saveClassifierOptions(configurationId: number, file: File): Observable<CreateClassifierOptionsResultDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('classifiersOptionsFile', file, file.name);
    formData.append('configurationId', configurationId.toString());
    return this.http.post<CreateClassifierOptionsResultDto>(this.serviceUrl + '/save', formData, { headers: headers });
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
