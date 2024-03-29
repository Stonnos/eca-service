import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClassifierListComponent } from "./components/classifier-list.component";
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersService } from "./services/classifiers.service";
import { FilterModule } from "../filter/filter.module";
import { RequestStatusesStatisticsModule } from "../request-statuses-statistics/request-statuses-statistics.module";
import { SharedModule } from "../common/shared.module";
import { CreateClassifierModule } from "../create-classifier/create-classifier.module";
import { CreateOptimalClassifierModule } from "../create-optimal-classifier/create-optimal-classifier.module";

@NgModule({
    imports: [
        CommonModule,
        SharedModule,
        FormsModule,
        UiComponentsModule,
        FilterModule,
        RequestStatusesStatisticsModule,
        CreateClassifierModule,
        CreateOptimalClassifierModule
    ],
    declarations: [
        ClassifierListComponent
    ],
    exports: [
        ClassifierListComponent
    ],
    providers: [
        ClassifiersService
    ]
})
export class ClassifiersModule {
}
