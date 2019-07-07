import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsRequestDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";
import { PageRequestService } from "../../common/services/page-request.service";

@Injectable()
export class ClassifierOptionsRequestService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getClassifiersOptionsRequests(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsRequestDto>>(this.serviceUrl + '/classifiers-options-requests', options);
  }
}
