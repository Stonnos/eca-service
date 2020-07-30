import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsRequestDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { PageRequestService } from "../../common/services/page-request.service";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class ClassifierOptionsRequestService {

  private serviceUrl = environment.serverUrl;

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getClassifiersOptionsRequests(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsRequestDto>>(this.serviceUrl + '/classifiers-options-requests', options);
  }
}
