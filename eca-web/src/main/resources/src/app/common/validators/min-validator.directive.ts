import { Directive, Input } from '@angular/core';
import { NG_VALIDATORS, Validator, FormControl } from '@angular/forms';

@Directive({
  selector: '[minNumber][formControlName],[minNumber][formControl],[minNumber][ngModel]',
  providers: [{provide: NG_VALIDATORS, useExisting: MinNumberValidator, multi: true}]
})
export class MinNumberValidator implements Validator {

  @Input()
  public minNumber: number;
  @Input()
  public minInclusive: boolean;

  public validate(control: FormControl): {[key: string]: any} {
    let value = control.value;
    if (this.minNumber != null && value != null) {
      if (this.minInclusive) {
        return value < this.minNumber ? {"minNumber": true} : null;
      } else {
        return value <= this.minNumber ? {"minNumber": true} : null;
      }
    }
    return null;
  }
}
