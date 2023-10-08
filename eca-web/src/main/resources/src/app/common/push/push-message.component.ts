import { Component, OnInit } from '@angular/core';
import { Router} from "@angular/router";
import { PushRequestDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { PushMessageType } from "../util/push-message.type";
import { PushVariables } from "../util/push-variables";
import { RouterPaths } from "../../routing/router-paths";
import { MessageService } from "primeng/api";
import { PushRoute } from "../model/push.model";

@Component({
  selector: 'app-push-messages',
  templateUrl: './push-message.component.html',
  styleUrls: ['./push-message.component.scss']
})
export class PushMessageComponent implements OnInit {

  private pushRoutes: PushRoute[] = [];

  public constructor(private router: Router,
                     private messageService: MessageService) {
    this.initPushRoutes();
  }

  public ngOnInit(): void {
  }

  public onRoute(pushRequestDto: PushRequestDto): void {
    const pushRoute = this.pushRoutes.filter((route: PushRoute) => route.messageType == pushRequestDto.messageType).pop();
    if (pushRoute) {
      const id = pushRequestDto.additionalProperties[pushRoute.itemIdPropertyName];
      this.router.navigate([pushRoute.routePath, id]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle push message ${pushRequestDto.messageType} as link`});
    }
  }

  private initPushRoutes(): void {
    this.pushRoutes = [
      {
        messageType: PushMessageType.EXPERIMENT_STATUS_CHANGE,
        itemIdPropertyName: PushVariables.EVALUATION_ID,
        routePath: RouterPaths.EXPERIMENT_DETAILS_URL
      },
      {
        messageType: PushMessageType.EVALUATION_STATUS_CHANGE,
        itemIdPropertyName: PushVariables.EVALUATION_ID,
        routePath: RouterPaths.EVALUATION_DETAILS_URL
      },
      {
        messageType: PushMessageType.CLASSIFIER_CONFIGURATION_CHANGE,
        itemIdPropertyName: PushVariables.CLASSIFIERS_CONFIGURATION_ID,
        routePath: RouterPaths.CLASSIFIERS_CONFIGURATION_DETAILS_URL
      }
    ];
  }
}
