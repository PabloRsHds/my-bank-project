import { AbstractControl, ValidationErrors } from '@angular/forms';

export function passwordValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.value;
  const errors: ValidationErrors = {};

  if (!password) return null;

  if (password.length < 8) {
    errors['minLength'] = true;
  }

  if (!/[A-Z]/.test(password)) {
    errors['uppercase'] = true;
  }

  if (!/[a-z]/.test(password)) {
    errors['lowercase'] = true;
  }

  if (!/\d/.test(password)) {
    errors['number'] = true;
  }

  if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    errors['symbol'] = true;
  }

  return Object.keys(errors).length > 0 ? errors : null;
}
