import { AbstractControl, ValidationErrors } from '@angular/forms';

export function dateValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value;

  if (!value || value.trim().length !== 10) {
    return { invalidFormat: true };
  }

  // Confirma formato dd/MM/yyyy
  const dateRegex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
  const match = dateRegex.exec(value);

  if (!match) {
    return { invalidFormat: true };
  }

  const day = parseInt(match[1], 10);
  const month = parseInt(match[2], 10) - 1; // meses em JS são de 0 a 11
  const year = parseInt(match[3], 10);

  const dateObj = new Date(year, month, day);

  // Garante que não seja tipo 31/02/2020
  if (
    dateObj.getFullYear() !== year ||
    dateObj.getMonth() !== month ||
    dateObj.getDate() !== day
  ) {
    return { invalidDate: true };
  }

  const today = new Date();
  const adultCutoff = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());

  if (dateObj > today) {
    return { futureDate: true };
  }

  if (dateObj > adultCutoff) {
    return { underAge: true };
  }

  return null;
}
