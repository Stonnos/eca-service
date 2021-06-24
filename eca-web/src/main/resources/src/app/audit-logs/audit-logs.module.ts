import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FilterModule } from "../filter/filter.module";
import { SharedModule } from "../common/shared.module";
import { AuditLogsComponent } from "./components/audit-logs.component";
import { AuditLogService } from "./services/audit-log.service";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    AuditLogsComponent
  ],
  providers: [
    AuditLogService
  ]
})
export class AuditLogsModule {
}
