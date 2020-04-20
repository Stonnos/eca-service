import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsDto, CreateExperimentResultDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

@Injectable()
export class ClassifierOptionsService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient) {
  }

  public getClassifiersOptions(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString());
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsDto>>(this.serviceUrl + '/experiment/classifiers-options/page', options);
  }

  public saveClassifierOptions(configurationId: number, file: File): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const formData = new FormData();
    formData.append('classifiersOptionsFile', file, file.name);
    formData.append('configurationId', configurationId.toString());
    return this.http.post<CreateExperimentResultDto>(this.serviceUrl + '/save', formData, { headers: headers });
  }
}
