import { Directive, Input } from '@angular/core';
import { NG_VALIDATORS, Validator, FormControl } from '@angular/forms';

@Directive({
  selector: '[minNumber][formControlName],[minNumber][formControl],[minNumber][ngModel]',
  providers: [{provide: NG_VALIDATORS, useExisting: MinNumberValidator, multi: true}]
})
export class MinNumberValidator implements Validator {

  @Input()
  public minNumber: number;

  public validate(control: FormControl): {[key: string]: any} {
    let value = control.value;
    return this.minNumber != null && value != null && value < this.minNumber ? {"minNumber": true} : null;
  }
}
