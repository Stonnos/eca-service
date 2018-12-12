import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FilterFieldDto, FilterTemplateType } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { CookieService } from "ngx-cookie-service";

@Injectable()
export class FilterService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  public getFilterFields(templateType: FilterTemplateType): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    let params = new HttpParams().set('templateType', templateType);
    const options = { headers: headers, params: params };
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/filters/template', options);
  }
}
