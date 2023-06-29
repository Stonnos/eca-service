import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FieldService } from "./services/field.service";
import { ReportsService } from "./services/report.service";
import { RequestStatusComponent } from "./request-status/components/request-status.component";
import { ValidationService } from "./services/validation.service";
import { EqualValidator } from "./validators/equal-validator.directive";
import { SearchQueryComponent } from "./search-query/search-query.component";
import { UploadTrainingDataComponent } from "./upload-training-data/upload-training-data.component";
import { ConfirmDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { EditableInputTextComponent } from "./editable-input-text/editable-input-text.component";
import { InfoPanelComponent } from "./info-panel/info-panel.component";
import { ErrorHandler } from "./services/error-handler";
import { WebAppService } from "./services/web-app.service";
import { ClassifierOptionsDetails } from "./classifier-options-details/classifier-options-details";
import { MinNumberValidator } from "./validators/min-validator.directive";
import { MaxNumberValidator } from "./validators/max-validator.directive";
import { ClassifierOptionsInfo } from "./classifier-options-info/classifier-options-info";
import { EvaluationMethodInfo } from "./evaluation-method-info/evaluation-method-info";
import { InstancesInfo } from "./instances-info/instances-info";
import { WsService } from "./websockets/ws.service";
import { PushService } from "./push/push.service";
import { PushMessageComponent } from "./push/push-message.component";
import { EventService } from "./event/event.service";
import { EventHandler } from "./event/event.handler";
import { PushTokenService } from "./push/push-token.service";
import { ChartBarStatisticsComponent } from "./chart-bar-statistics/chart-bar-statistics.component";
import { NotBlankValidator } from "./validators/not-blank-validator.directive";
import { InstancesAutocompleteComponent } from "./instances-autocomplete/instances-autocomplete.component";
import { InstancesInfoService } from "./instances-info/services/instances-info.service";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    RequestStatusComponent,
    SearchQueryComponent,
    UploadTrainingDataComponent,
    EqualValidator,
    MinNumberValidator,
    MaxNumberValidator,
    NotBlankValidator,
    ConfirmDialogComponent,
    EditableInputTextComponent,
    InfoPanelComponent,
    ClassifierOptionsDetails,
    ClassifierOptionsInfo,
    EvaluationMethodInfo,
    InstancesInfo,
    PushMessageComponent,
    ChartBarStatisticsComponent,
    InstancesAutocompleteComponent
  ],
  exports: [
    RequestStatusComponent,
    SearchQueryComponent,
    UploadTrainingDataComponent,
    EqualValidator,
    MinNumberValidator,
    MaxNumberValidator,
    NotBlankValidator,
    ConfirmDialogComponent,
    EditableInputTextComponent,
    InfoPanelComponent,
    ClassifierOptionsDetails,
    ClassifierOptionsInfo,
    EvaluationMethodInfo,
    InstancesInfo,
    PushMessageComponent,
    ChartBarStatisticsComponent,
    InstancesAutocompleteComponent
  ],
  providers: [
    FieldService,
    ReportsService,
    ValidationService,
    InstancesInfoService,
    ErrorHandler,
    WebAppService,
    WsService,
    PushService,
    PushTokenService,
    EventService,
    EventHandler
  ]
})
export class SharedModule {
}
