import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { catchError } from "rxjs/internal/operators";
import { Router } from "@angular/router";
import { EMPTY } from "rxjs/internal/observable/empty";
import { throwError } from "rxjs/internal/observable/throwError";
import { CookieService } from "ngx-cookie-service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router, private cookieService: CookieService) {
  }

  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(error => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        this.cookieService.delete('access_token');
        this.router.navigate(['/login']);
        return EMPTY;
      }
      return throwError(error);
    }));
  }
}
