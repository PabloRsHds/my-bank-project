 import { AbstractControl, ValidationErrors } from '@angular/forms';

export function cpfValidator(control: AbstractControl): ValidationErrors | null {
  const cpf = control.value?.replace(/\D/g, ''); // Remove tudo que não é número

  if (!cpf) {
    return null; // Campo vazio, deixa o required cuidar disso
  }

  if (!cpf || cpf.length !== 11) {
    return { cpfValidator: true };
  }

  if (/^(\d)\1{10}$/.test(cpf)) {  // CPF com todos números iguais é inválido
    return { cpfValidator: true };
  }

  let soma = 0;
  let resto;

  for (let i = 1; i <= 9; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  }

  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(9, 10))) {
    return { cpfValidator: true };
  }

  soma = 0;
  for (let i = 1; i <= 10; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  }

  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(10, 11))) {
    return { cpfValidator: true };
  }

  return null; // CPF válido
}
