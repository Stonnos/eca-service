import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifiersConfigurationDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

@Injectable()
export class ClassifiersConfigurationsService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/experiment/classifiers-configurations';

  public constructor(private http: HttpClient) {
  }

  public getClassifiersConfigurations(pageRequest: PageRequestDto): Observable<PageDto<ClassifiersConfigurationDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString());
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifiersConfigurationDto>>(this.serviceUrl + '/list', options);
  }

  public deleteConfiguration(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    let params = new HttpParams().set('id', id.toString());
    const options = { headers: headers, params: params };
    return this.http.delete(this.serviceUrl + '/delete', options);
  }

  public setActive(id: number): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const formData = new FormData();
    formData.append('id', id.toString());
    return this.http.post(this.serviceUrl + '/set-active', formData, { headers });
  }
}
