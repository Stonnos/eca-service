import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto,
  ExperimentResultsDetailsDto,
  AttributeMetaInfoDto,
  AttributeValueMetaInfoDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService, SelectItem } from 'primeng/api';
import { ActivatedRoute, Router } from "@angular/router";
import { finalize } from "rxjs/internal/operators";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { RouterPaths } from "../../routing/router-paths";
import { ExperimentFields } from "../../common/util/field-names";
import { FieldLink } from "../../common/model/field-link";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";
import { AttributeMetaInfoModel } from '../../classify-instance/model/attribute-meta-info.model';
import { AttributesInfoMapper } from '../../common/instances-info/services/attributes-info.mapper';
import { InstancesInfoService } from '../../common/instances-info/services/instances-info.service';

@Component({
  selector: 'app-experiment-results-details',
  templateUrl: './experiment-results-details.component.html',
  styleUrls: ['./experiment-results-details.component.scss']
})
export class ExperimentResultsDetailsComponent implements OnInit, FieldLink {

  private readonly experimentResultsId: number;

  public experimentFields: any[] = [];
  public loading: boolean = false;

  public experimentResultsDetailsDto: ExperimentResultsDetailsDto;

  public classValues: SelectItem[] = [];

  public attributeMetaInfoModels: AttributeMetaInfoModel[] = [];

  public linkColumns: string[] = [ExperimentFields.REQUEST_ID];

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private router: Router,
                     private fieldService: FieldService,
                     private attributesInfoMapper: AttributesInfoMapper,
                     private instancesInfoService: InstancesInfoService) {
    this.experimentResultsId = this.route.snapshot.params.id;
    this.initExperimentFields();
  }

  public ngOnInit(): void {
    this.getExperimentResultsDetails();
  }

  public getExperimentResultsDetails(): void {
    this.loading = true;
    this.experimentsService.getExperimentResultsDetails(this.experimentResultsId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (experimentResultsDetailsDto: ExperimentResultsDetailsDto) => {
          this.experimentResultsDetailsDto = experimentResultsDetailsDto;
          this.getClassValues();
          this.getInputAttributes();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getExperimentValue(field: string) {
    const experimentDto: ExperimentDto = this.experimentResultsDetailsDto && this.experimentResultsDetailsDto.experimentDto;
    if (field == ExperimentFields.EVALUATION_METHOD_DESCRIPTION) {
      return Utils.getEvaluationMethodDescription(experimentDto.evaluationMethod, experimentDto.numFolds, experimentDto.numTests);
    } else {
      return this.fieldService.getFieldValue(field, experimentDto, Utils.MISSING_VALUE);
    }
  }

  public isLink(field: string): boolean {
    return this.linkColumns.includes(field);
  }

  public onLink(field: string): void {
    if (field == ExperimentFields.REQUEST_ID) {
      this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, this.experimentResultsDetailsDto.experimentDto.id]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${field} as link`});
    }
  }

  public getClassValues(): void {
    this.instancesInfoService.getClassValues(this.experimentResultsDetailsDto.experimentDto.instancesInfo.id)
      .subscribe({
        next: (classValues: AttributeValueMetaInfoDto[]) => {
          this.classValues = this.attributesInfoMapper.mapValues(classValues);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getInputAttributes(): void {
    this.instancesInfoService.getInputAttributes(this.experimentResultsDetailsDto.experimentDto.instancesInfo.id)
      .subscribe({
        next: (attributeMetaInfoList: AttributeMetaInfoDto[]) => {
          this.attributeMetaInfoModels = this.attributesInfoMapper.mapAttributes(attributeMetaInfoList);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: ExperimentFields.REQUEST_ID, label: "UUID заявки" },
      { name: ExperimentFields.EXPERIMENT_TYPE_DESCRIPTION, label: "Тип эксперимента" },
      { name: ExperimentFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности" }
    ];
  }
}
