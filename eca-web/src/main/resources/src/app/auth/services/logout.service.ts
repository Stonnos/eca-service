import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationKeys} from "../model/auth.keys";
import {EventService} from "../../common/event/event.service";
import {EventType} from "../../common/event/event.type";

@Injectable()
export class LogoutService {

  public constructor(private router: Router,
                     private eventService: EventService) {
  }

  public logout() {
    localStorage.removeItem(AuthenticationKeys.USER);
    localStorage.removeItem(AuthenticationKeys.ACCESS_TOKEN);
    localStorage.removeItem(AuthenticationKeys.REFRESH_TOKEN);
    this.eventService.publishEvent(EventType.LOGOUT);
    this.router.navigate(['/login']);
  }
}
