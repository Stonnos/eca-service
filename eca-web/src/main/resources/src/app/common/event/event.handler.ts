import { Injectable } from "@angular/core";
import { EventService } from "./event.service";
import { PushService } from "../push/push.service";
import { MessageService } from "primeng/api";
import { EventType } from "./event.type";
import { Logger } from "../util/logging";
import { UsersService } from "../../users/services/users.service";
import { UserDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class EventHandler {

  public constructor(private eventService: EventService,
                     private pushService: PushService,
                     private usersService: UsersService,
                     private messageService: MessageService) {
  }

  public eventSubscribe() {
    return this.eventService.eventSubscribe()
      .subscribe({
        next: (eventType: EventType) => {
          Logger.debug(`Handle event ${eventType}`);
          switch (eventType) {
            case EventType.INIT_PUSH:
              this.pushService.init();
              break;
            case EventType.TOKEN_REFRESHED:
              this.handleTokenRefreshedEvent();
              break;
            case EventType.CLOSE_PUSH:
            case EventType.TOKEN_EXPIRED:
            case EventType.LOGOUT:
              this.pushService.close();
              break;
            default:
              this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: `Can't handle event ${eventType}` });
          }
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private handleTokenRefreshedEvent(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        if (!user.pushEnabled) {
          Logger.debug('Pushes are disabled for user. Skipped push reconnection after token has been refreshed');
        } else {
          Logger.debug('Starting push reconnection after token has been refreshed');
          this.pushService.init();
        }
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    });
  }
}
