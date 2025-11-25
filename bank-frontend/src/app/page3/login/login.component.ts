import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterMModule } from '../../router/router-m/router-m.module';
import { cpfValidator } from '../../validators/cpfValidator';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { LoginService } from '../../service/login/login.service';


@Component({
  selector: 'app-login',
  imports: [CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterMModule,
    NgxMaskDirective],
  providers: [provideNgxMask({dropSpecialCharacters:false})],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginForm!: FormGroup;
  showPassword = false;
  showError = false;

  constructor(
    private service:LoginService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private router: Router) { }


  // Configuração do formulário
  configurationForm() {
    this.loginForm = this.formBuilder.group({
      cpf: ['', [Validators.required, cpfValidator]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    this.directLogin();
    this.configurationForm();
  }

  //Se o usuário estiver logado, ele vai para a página de cliente
  // sem precisar fazer login
  directLogin(){
    if (localStorage.getItem('accessToken') &&
        localStorage.getItem('refreshToken') &&
        localStorage.getItem('userId')) {
      this.router.navigate(['my-bank.com.br/client']);
    }
  }

  login() {

    if (this.loginForm.invalid) {
      return; // Não prossegue se o formulário for inválido
    }

    this.service.login(this.loginForm.value).subscribe({ //Envia os dados do formulário para o backend
      next: (response) => {

        localStorage.setItem('accessToken', response.accessToken); //Salva o access token
        localStorage.setItem('refreshToken', response.refreshToken); //Salva o refresh token

        this.snackBar.open(
          'Login Sucessfuly',
          '', {
          duration: 2000,
          panelClass: ['snackbar-success'] });

        this.service.getIdWithCpf(this.loginForm.value.cpf).subscribe(userId => {
          localStorage.setItem('userId', userId);
          this.router.navigate(['my-bank.com.br/client']);
        })
      },
      error: (err : Error) => {
        this.snackBar.open(
          err.message,
          '', {
            duration: 5000,
            panelClass: ['snackbar-danger'] });
      }
    })
  }

  //Volta para a pagina de cadastro e abrindo a aba de cadastro
  goToRegister(){
    this.router.navigate(['my-bank.com.br'], {queryParams: {register: true}});
  }

  //Volta para a página inicial
  return() {
    this.router.navigate(['my-bank.com.br']);
  }
}
