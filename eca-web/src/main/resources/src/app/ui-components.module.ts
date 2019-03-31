import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConfirmationService } from 'primeng/api';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { ChartModule } from 'primeng/chart';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageService } from "primeng/components/common/messageservice";
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { FileUploadModule } from 'primeng/fileupload';
import { GrowlModule } from 'primeng/growl';
import { InputTextModule } from 'primeng/inputtext';
import { KeyFilterModule } from 'primeng/keyfilter';
import { LightboxModule } from 'primeng/lightbox';
import { MenubarModule } from 'primeng/menubar';
import { MultiSelectModule } from 'primeng/multiselect';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { PanelModule } from 'primeng/panel';
import { PasswordModule } from 'primeng/password';
import { InputSwitchModule, ListboxModule, TooltipModule } from 'primeng/primeng';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { CardModule } from "primeng/card";
import { FieldsetModule } from 'primeng/fieldset';
import { ScrollPanelModule } from 'primeng/scrollpanel';

@NgModule({
  imports: [
    CommonModule
  ],
  providers: [
    ConfirmationService,
    MessageService
  ],
  exports: [
    ButtonModule,
    InputTextModule,
    MenubarModule,
    PanelModule,
    PasswordModule,
    TableModule,
    MultiSelectModule,
    OverlayPanelModule,
    CalendarModule,
    DialogModule,
    RadioButtonModule,
    TabViewModule,
    CardModule,
    ChartModule,
    ProgressBarModule,
    LightboxModule,
    ConfirmDialogModule,
    KeyFilterModule,
    DropdownModule,
    AutoCompleteModule,
    TooltipModule,
    FileUploadModule,
    GrowlModule,
    ProgressSpinnerModule,
    ListboxModule,
    InputSwitchModule,
    CheckboxModule,
    FieldsetModule,
    ScrollPanelModule
  ]
})
export class UiComponentsModule {
}
