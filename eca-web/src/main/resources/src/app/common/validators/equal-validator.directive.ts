import { Directive, forwardRef, Attribute, Input } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[validateEqual][formControlName],[validateEqual][formControl],[validateEqual][ngModel]',
  providers: [
    {provide: NG_VALIDATORS, useExisting: forwardRef(() => EqualValidator), multi: true}
  ]
})
export class EqualValidator implements Validator {

  @Input('source')
  public source: boolean;

  public constructor(@Attribute('validateEqual') public validateEqual: string) {
  }

  public validate(control: AbstractControl): { [key: string]: any } {
    let value = control.value;
    let element = control.root.get(this.validateEqual);
    if (element && value && value !== element.value && !this.source) {
      return {validateEqual: true}
    }
    if (element && value && value === element.value && this.source && element.errors) {
      delete element.errors['validateEqual'];
      if (!Object.keys(element.errors).length) {
        element.setErrors(null);
      }
    }
    if (element && value && value !== element.value && this.source) {
      element.setErrors({validateEqual: true});
    }
    return null;
  }
}
