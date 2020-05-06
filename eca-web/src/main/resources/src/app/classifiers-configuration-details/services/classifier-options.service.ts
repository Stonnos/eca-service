import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsDto, CreateClassifierOptionsResultDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";
import { PageRequestService } from "../../common/services/page-request.service";

@Injectable()
export class ClassifierOptionsService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/experiment/classifiers-options';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getClassifiersOptions(configurationId: number, pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest)
      .set('configurationId', configurationId.toString());
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsDto>>(this.serviceUrl + '/page', options);
  }

  public saveClassifierOptions(configurationId: number, file: File): Observable<CreateClassifierOptionsResultDto> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const formData = new FormData();
    formData.append('classifiersOptionsFile', file, file.name);
    formData.append('configurationId', configurationId.toString());
    return this.http.post<CreateClassifierOptionsResultDto>(this.serviceUrl + '/save', formData, { headers: headers });
  }

  public deleteClassifierOptions(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    let params = new HttpParams().set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.delete(this.serviceUrl + '/delete', options);
  }
}
