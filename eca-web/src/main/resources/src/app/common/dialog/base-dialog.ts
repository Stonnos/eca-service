import { EventEmitter } from '@angular/core';

export interface BaseDialog {

  visible: boolean;

  visibilityChange: EventEmitter<boolean>;

  hide(): void;
}
