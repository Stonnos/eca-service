import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-editable-input-text',
  templateUrl: './editable-input-text.component.html',
  styleUrls: ['./editable-input-text.component.scss']
})
export class EditableInputTextComponent implements OnInit {

  @Input()
  public value: string;

  @Output()
  public submitEvent: EventEmitter<string> = new EventEmitter<string>();

  public editableValue: string;

  public editMode: boolean = false;

  public ngOnInit() {
    this.editableValue = this.value;
  }

  public enableEditMode(): void {
    this.editMode = true;
  }

  public submit(): void {
    this.editMode = false;
    this.submitEvent.emit(this.editableValue);
  }

  public cancel(): void {
    this.editMode = false;
    this.editableValue = this.value;
  }
}
