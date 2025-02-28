import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { InstancesInfoDto, ClassifyInstanceResultDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AttributeMetaInfoModel } from '../model/attribute-meta-info.model';
import { ClassifyInstanceService } from '../../common/services/classify-instance.service';
import { NgForm } from '@angular/forms';
import { Logger } from '../../common/util/logging';
import { ClassifyInstanceRequestDto, ClassifyInstanceValueDto } from '../model/classify-instance-request.model';
import { finalize } from 'rxjs/internal/operators';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-classify-instance',
  templateUrl: './classify-instance.component.html',
  styleUrls: ['./classify-instance.component.scss']
})
export class ClassifyInstanceComponent implements OnInit {

  @Input()
  public modelId: number;

  @Input()
  public classifyInstanceService: ClassifyInstanceService;

  @Input()
  public instancesInfoDto: InstancesInfoDto;

  @Input()
  public attributeMetaInfoModels: AttributeMetaInfoModel[] = [];

  @ViewChild(NgForm, { static: true })
  private form: NgForm;

  public submitted: boolean = false;

  public loading: boolean = false;

  public classifyInstanceResultDto: ClassifyInstanceResultDto;

  public constructor(private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public classify(): void {
    this.submitted = true;
    if (this.form.valid) {
      this.submitted = false;
      const values = this.attributeMetaInfoModels.map((attributeMetaInfoModel: AttributeMetaInfoModel) => {
        const value = this.transformValue(attributeMetaInfoModel);
        return new ClassifyInstanceValueDto(attributeMetaInfoModel.index, value);
      });
      const classifyInstanceRequestDto = new ClassifyInstanceRequestDto(this.modelId, values);
      Logger.debug(`Classify instance ${JSON.stringify(classifyInstanceRequestDto)}`);
      this.classifyRequest(classifyInstanceRequestDto);
    }
  }

  public reset(): void {
    this.form.reset();
    this.classifyInstanceResultDto = null;
    this.submitted = false;
  }

  public getValidationErrors(state: any): string[] {
    let messages: string[] = [];
    if (state && state.errors) {
      for (let errorName in state.errors) {
        if (errorName === 'required') {
          messages.push(`Заполните поле`);
        }
      }
    }
    return messages;
  }

  private transformValue(attributeMetaInfoModel: AttributeMetaInfoModel): number {
    if (attributeMetaInfoModel.type.value == 'DATE') {
      return attributeMetaInfoModel.currentValue.getTime();
    }
    return attributeMetaInfoModel.currentValue;
  }

  private classifyRequest(classifyInstanceRequestDto: ClassifyInstanceRequestDto): void {
    this.loading = true;
    this.classifyInstanceService.classifyInstance(classifyInstanceRequestDto)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (classifyInstanceResultDto: ClassifyInstanceResultDto) => {
          this.classifyInstanceResultDto = classifyInstanceResultDto;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
