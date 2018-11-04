import { Component, OnInit } from '@angular/core';
import {
  ClassifierOptionsDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifierOptionsService } from "../services/classifier-options.service";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";

@Component({
  selector: 'app-classifier-options',
  templateUrl: './classifier-options.component.html',
  styleUrls: ['./classifier-options.component.scss']
})
export class ClassifierOptionsComponent extends BaseListComponent<ClassifierOptionsDto> implements OnInit {

  public selectedOptions: ClassifierOptionsDto;

  public constructor(private classifierOptionsService: ClassifierOptionsService,
                     private messageService: MessageService) {
    super();
    this.defaultSortField = "creationDate";
    this.linkColumns = ["optionsName"];
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPage(pageRequest: PageRequestDto) {
    this.classifierOptionsService.getClassifiersOptions(pageRequest).subscribe((pageDto: PageDto<ClassifierOptionsDto>) => {
      this.items = pageDto.content;
      this.total = pageDto.totalCount;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  public onLink(column: string, classifierOptionsDto: ClassifierOptionsDto) {

  }

  public onSelect(event, classifierOptionsDto: ClassifierOptionsDto, overlayPanel: OverlayPanel) {
    this.selectedOptions = classifierOptionsDto;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: "optionsName", label: "Options name" },
      { name: "version", label: "Options version" },
      { name: "creationDate", label: "Creation date" }
    ];
  }
}
