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
import { EMPTY } from "rxjs/internal/observable/empty";
import { throwError } from "rxjs/internal/observable/throwError";
import { LogoutService } from "../services/logout.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private logoutService: LogoutService) {
  }

  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(error => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        this.logoutService.logout();
        return EMPTY;
      }
      return throwError(error);
    }));
  }
}
