import { Component, inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { ConfirmCodeService } from '../../service/confirm-code/confirm-code.service';



@Component({
  selector: 'app-confirm',
  imports: [FormsModule, ReactiveFormsModule,CommonModule],
  templateUrl: './confirm.component.html',
  styleUrl: './confirm.component.css'
})
export class ConfirmComponent {

  confirmForm! : FormGroup
  //vai armazenar o email que vem pela url, e ele será usado para, fazer uma verificação, onde
  //se o usuário já tiver sido verificado, o serviço checkUserEmailVerification será chamado
  //e o qr code será exposto, isso evita que o usuário tente digitar o código novamente
  email! : string;
  //ShowForm = true, mostra o formulário de digitação do código
  //ShowForm = false, mostra o qr code
  showForm = true
  private snackBar = inject(MatSnackBar)


  constructor(
    private service:ConfirmCodeService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,){}

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      this.email = params['email']; // agora pega ?email=..
    });

    this.checkEmailVerification();

    console.log(this.email);
    this.confirm(); //Inicia o formulário
  }

  checkEmailVerification() {
    if (!this.email) return;

    this.service.checkUserEmailVerification(this.email).subscribe({
      next: (response) => {

        if (response === 'true') {
          this.showForm = false; //Se o usuário ja tiver sido verificado, mostra o qr code
          return;
        } else {
          this.showForm = true;
        }
      },
      error: (err) => {
        console.error(err);
        this.showForm = true; // fallback
      }
    });
  }

  // Volta para a página anterior
  return() {
    window.history.go(-1);
  }

  confirm(){ //Inicia o formulário e suas validações
    this.confirmForm = this.formBuilder.group({
      code1: ['', [Validators.required]],
      code2: ['', [Validators.required]],
      code3: ['', [Validators.required]],
      code4: ['', [Validators.required]],
      code5: ['', [Validators.required]],
      code6: ['', [Validators.required]]
    })
  }

  //Verifica o código e se tudo estiver correto, verifica o usuário e retorna o qr code
  confirmCode(){
    const values = this.confirmForm.value; //Pega os valores do formulário
    const fullCode = Object.values(values).join(''); //Converte o objeto em uma string e remove os espaços

    this.service.confirmCode({email : this.email, code : fullCode}).subscribe({
      next: (response) => {
        this.snackBar.open(
          'Verification Code Sucessfuly',
          '', {
            duration: 3000,
            panelClass: ['snackbar']}),
            this.showForm = false //Esconde o formulário e mostra o qr code
      },
      error: (error) => {
        this.snackBar.open(
          'This code is incorrect',
          '', {
            duration: 3000,
            panelClass: ['snackbar']}),
            this.showForm = true //Volta a mostrar o formulário porque o código é incorreto
          }}
    );
  }

  //O código precisa ser verificado = true;
  //Se o código foi verificado com sucesso = false, aí eu mostro ao usuário o QR code
  confirmCodeVerification(){
    return this.showForm;
  }


  //Limpa o formulário e volta para a pagina de confirmação
  clearCode(){
    this.confirmForm.reset();
  }


  //Reenvia o código
  resendCode(){

    this.service.resendCode({email : this.email}).subscribe({
      next: (response) => {
        this.snackBar.open(
          'New verification code sent to email',
          '', {
            duration: 5000,
            horizontalPosition: 'center',
            panelClass: ['snackbar']})
        ,
        this.confirmForm.reset();
      },
      error: (err : Error) => {
        this.snackBar.open(
          err.message,
          '', {
            duration: 2000,
            panelClass: ['snackbar']});
      }
    })
  }
}
