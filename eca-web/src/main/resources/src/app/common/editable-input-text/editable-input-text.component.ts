import { Component, Input, OnInit, EventEmitter, Output, ViewChild } from '@angular/core';
import { NgForm } from "@angular/forms";

@Component({
  selector: 'app-editable-input-text',
  templateUrl: './editable-input-text.component.html',
  styleUrls: ['./editable-input-text.component.scss']
})
export class EditableInputTextComponent implements OnInit {

  @ViewChild(NgForm, { static: true })
  private form: NgForm;

  private mouseDownEvent: Event;

  @Input()
  public textId: string = 'editField';

  @Input()
  public textName: string = 'editField';

  @Input()
  public value: string;

  @Input()
  public pattern: string;

  @Input()
  public maxLength: number = 255;

  @Input()
  public textDisabled: boolean = false;

  @Input()
  public requiredFieldErrorMessage: string = 'Обязательное поле';

  @Input()
  public invalidPatternErrorMessages: string[] = [];

  @Output()
  public submitEvent: EventEmitter<string> = new EventEmitter<string>();

  public editableValue: string = '';

  public editMode: boolean = false;

  public submitted: boolean = false;

  public ngOnInit() {
    this.editableValue = this.value;
  }

  public mouseDown(event: Event): void {
    this.mouseDownEvent = event;
  }

  public enableEditMode(): void {
    this.editMode = true;
  }

  public disableEditMode(event: Event): void {
    if (this.mouseDownEvent) {
      event.preventDefault();
    } else {
      this.cancel();
    }
  }

  public submit(): void {
    this.mouseDownEvent = null;
    this.submitted = true;
    if (this.isValid()) {
      this.submitEvent.emit(this.editableValue);
      this.editMode = false;
      this.submitted = false;
    }
  }

  public cancel(): void {
    this.mouseDownEvent = null;
    this.editMode = false;
    this.submitted = false;
    this.editableValue = this.value;
  }

  public isValid(): boolean {
    return this.form.valid;
  }
}
