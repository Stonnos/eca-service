import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EventService } from "../../common/event/event.service";
import { EventType } from "../../common/event/event.type";

@Injectable()
export class LogoutService {

  public constructor(private router: Router,
                     private eventService: EventService) {
  }

  public logout() {
    this.eventService.publishEvent(EventType.LOGOUT);
    this.router.navigate(['/login']);
  }
}
