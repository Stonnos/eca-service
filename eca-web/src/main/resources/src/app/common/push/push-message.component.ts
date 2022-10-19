import { Component, OnInit } from '@angular/core';
import { Router} from "@angular/router";
import { PushRequestDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { PushMessageType } from "../util/push-message.type";
import { PushVariables } from "../util/push-variables";
import { RouterPaths } from "../../routing/router-paths";
import { MessageService } from "primeng/api";

@Component({
  selector: 'app-push-messages',
  templateUrl: './push-message.component.html',
  styleUrls: ['./push-message.component.scss']
})
export class PushMessageComponent implements OnInit {

  public constructor(private router: Router,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public onLinkClick(pushRequestDto: PushRequestDto): void {
    if (pushRequestDto.messageType == PushMessageType.EXPERIMENT_STATUS_CHANGE) {
      const id = pushRequestDto.additionalProperties[PushVariables.EXPERIMENT_ID];
      this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, id]);
    } else if (pushRequestDto.messageType == PushMessageType.CLASSIFIER_CONFIGURATION_CHANGE) {
      const configurationId = pushRequestDto.additionalProperties[PushVariables.CLASSIFIERS_CONFIGURATION_ID];
      this.router.navigate([RouterPaths.CLASSIFIERS_CONFIGURATION_DETAILS_URL, configurationId]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle push message ${pushRequestDto.messageType} as link`});
    }
  }
}
