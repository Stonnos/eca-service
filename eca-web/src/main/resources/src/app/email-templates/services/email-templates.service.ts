import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EmailTemplateDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";

@Injectable()
export class EmailTemplatesService {

  private serviceUrl = environment.mailUrl + '/templates';

  public constructor(private http: HttpClient) {
  }

  public getTemplates(pageRequest: PageRequestDto): Observable<PageDto<EmailTemplateDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<EmailTemplateDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }
}
