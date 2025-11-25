import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { cpfValidator } from '../../validators/cpfValidator';
import { passwordValidator } from '../../validators/passwordValidator';
import { dateValidator } from '../../validators/dateValidator';
import { RouterMModule } from '../../router/router-m/router-m.module';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AnimateOnScrollDirective } from "../../directives/animate-on-scroll.directive";
import { RegisterService } from '../../service/register/register.service';



@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NgxMaskDirective,
    RouterMModule,
    AnimateOnScrollDirective
],
  //Eu coloco o provideNgxMask aqui para que ele seja usado em todo o projeto
  providers: [provideNgxMask({dropSpecialCharacters:false})],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit{

  sidebarOpen = false; //Variável para abrir e fechar o sidebar
  registerForm!: FormGroup; //Formulário
  private snackBar = inject(MatSnackBar);

  constructor(
    private service: RegisterService,
    private formBuilder: FormBuilder,
    private router : Router,
    private route : ActivatedRoute){}

  ngOnInit(){
    //Essa função é usada quando o usuário vem da pagina de login
    //Ela abre o sidebar se o usuário tiver clicado em "cadastre-se"
    this.route.queryParams.subscribe(params => {
      if (params['register'] === 'true'){
        this.openSidebar();
      }
    })

    this.configurationForm(); //Inicializa o formulário
  }

  //Configuração do formulário
  configurationForm(){
    this.registerForm = this.formBuilder.group({
      cpf: ['', [Validators.required, cpfValidator]],
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%+-]+@gmail\.com$/)]],
      password: ['', [Validators.required, passwordValidator]],
      phone: ['', [Validators.required, Validators.pattern('') ]],
      date: ['', [Validators.required, dateValidator]]
    });
  }

  //Função para abrir o sidebar
  openSidebar() {
    this.sidebarOpen = true;
  }

  //Função para fechar o sidebar
  closeSidebar() {
    this.sidebarOpen = false;
  }

  //Função de registro de usuário
  register(){
    if(this.registerForm.invalid){
      this.registerForm.markAllAsTouched(); //Marca todos os campos como tocados
      return;
    }

    this.service.register(this.registerForm.value)
    .subscribe({
      next: (response) => {
        this.snackBar.open(
          'Verification code sent to email',
          '', {
            duration: 3000,
            panelClass: ['snackbar']})
        this.router.navigate(['my-bank.com.br/confirm-email'],
          { queryParams : { email : this.registerForm.value.email}}) //Passa o email para a pagina de confirmação
      },
      error: (err : Error) => {
        this.snackBar.open(
          err.message,
          '', {
            duration: 5000,
            horizontalPosition: 'center',
            panelClass: ['snackbar-danger']});
      }
    });
  }



  //Tratamento de erros relacionados ao password
  getPasswordError(): string | null {
    const control = this.registerForm.get('password');
    if (!control || !control.touched) return null;

    if (control.hasError('minLength')) return 'Minimum 8 characters.';
    if (control.hasError('uppercase')) return 'Must contain at least one capital letter.';
    if (control.hasError('lowercase')) return 'Must contain at least one lowercase letter.';
    if (control.hasError('number')) return 'Must contain at least one number.';
    if (control.hasError('symbol')) return 'Must contain at least one symbol.';

    return null;
  }

  //Tratamento de erros relacionados a data
  getDateError(): string | null {
    const control = this.registerForm.get('date');
    if (!control || !control.touched) return null;

    if (control.hasError('invalidFormat')) return 'Invalid format. Use dd/MM/yyyy.';
    if (control.hasError('invalidDate')) return 'Invalid date.';
    if (control.hasError('futureDate')) return 'Date cannot be in the future';
    if (control.hasError('underAge')) return 'Under 18s cannot create an account';

    return null;
  }
}
