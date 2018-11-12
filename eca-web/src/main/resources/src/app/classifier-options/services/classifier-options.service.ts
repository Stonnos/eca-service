import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { CookieService } from "ngx-cookie-service";

@Injectable()
export class ClassifierOptionsService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  public getClassifiersOptions(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString());
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsDto>>(this.serviceUrl + '/experiment/classifiers-config/configs-page', options);
  }
}
