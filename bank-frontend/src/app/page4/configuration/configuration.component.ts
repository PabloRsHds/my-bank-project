import { ConfigurationService } from './../../service/configuration/configuration.service';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ResponseUser } from '../../dtos/user/ResponseUser';
import { Notifications } from '../../dtos/notification/Notifications';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms'
import { passwordValidator } from '../../validators/passwordValidator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { ResponseLoginHistory } from '../../dtos/login/ResponseLoginHistory';

@Component({
  selector: 'app-configuration',
  imports: [CommonModule, NgxMaskDirective, FormsModule, ReactiveFormsModule],
  providers: [provideNgxMask({dropSpecialCharacters:false})],
  templateUrl: './configuration.component.html',
  styleUrl: './configuration.component.css'
})
export class ConfigurationComponent {

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS DO USUÁRIO
  // =========================================================================
  isDarkTheme = false;
  userId?: string;
  admin = false;

  user: ResponseUser = {
    cpf: '',
    fullName: '',
    email: '',
    password: '',
    phone: '',
    date: ''
  };

  clientIsVerified = false;
  notificationCount: number = 0;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - FORMULÁRIOS
  // =========================================================================
  configurationPasswordForm!: FormGroup;
  configurationPhoneForm!: FormGroup;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - MODAIS E SIDEBARS
  // =========================================================================
  clickedButton = false;
  updatePassword = false;
  updatePhone = false;
  openSideBarNotification = false;
  openModalConfiguration = false;
  openSidebarMenu = false;
  deleteAccount = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS
  // =========================================================================
  notifications: Notifications[] = [];
  loginHistory: ResponseLoginHistory[] = [];

  // =========================================================================
  // LISTA DE CONFIGURAÇÕES INTERATIVAS
  // =========================================================================
  configList = [
    {id: 1, button: "Account", pressed: true},
    {id: 2, button: "Change yours configs", pressed: false},
    {id: 3, button: "History of logins", pressed: false},
    {id: 4, button: "Get out of everything", pressed: false}
  ]

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private service: ConfigurationService) {}

  // =========================================================================
  // CICLO DE VIDA - ngOnInit
  // =========================================================================
  ngOnInit() {
    if (typeof window !== 'undefined') {
      this.userId = localStorage.getItem('userId') ?? undefined;

      const theme = localStorage.getItem('theme');
      if (theme === 'dark') {
        this.isDarkTheme = true;
      } else {
        this.isDarkTheme = false;
      }
    }

    this.getUser();
    this.getUserAdm();
    this.checkVerificationClient();
    this.countNotifications();

    this.passwordInitialize();
    this.phoneInitialize();

    this.initializeLoginHistory();
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE USUÁRIO
  // =========================================================================

  // Obtém informações do usuário pelo ID
  getUser() {
    if (this.userId) {
      this.service.getUserWithId(this.userId).subscribe((user) => {
        this.user = user;
      });
    }
  }

  // Verifica se o usuário é administrador
  getUserAdm(){
    if (this.userId) {
      this.service.verifyIfUserIsAdm().subscribe({
        next: (response) => {
          if (response === true) {
            this.admin = true;
          } else {
            this.admin = false;
          }
        }
      })
    }
  }

  // Verifica se o usuário está verificado
  checkVerificationClient() {
    this.service.checkUserVerification().subscribe({
      next: (response) => {
        if (response === true){
          this.clientIsVerified = true
        } else {
          this.clientIsVerified = false
        }
      }
    })
  }

  // =========================================================================
  // SERVICOS - CONFIGURAÇÕES DA CONTA
  // =========================================================================

  // Obtém histórico de logins
  initializeLoginHistory() {
    this.service.loginHistory().subscribe({
      next: (response) => {
        this.loginHistory = response;
      }
    })
  }

  // Exclui permanentemente a conta do usuário
  deleteYourAccount(){
    this.service.deleteAccount().subscribe({
      next: () => {
        this.snackBar.open(
          'Account deleted successfully',
          '',
          { duration: 3000, panelClass: ['snackbar-success'] });

          setTimeout(() => {
            this.logout();
          }, 3000);
      }
    })
  }

  // =========================================================================
  // FORMULÁRIOS - INICIALIZAÇÃO
  // =========================================================================

  // Inicializa formulário de alteração de senha
  passwordInitialize(){
    this.configurationPasswordForm = this.formBuilder.group({
      password: ['', [Validators.required, passwordValidator]],
      oldPassword: ['', [Validators.required, passwordValidator]],
      confirmPassword: ['', [Validators.required, passwordValidator]]
    })
  }

  // Inicializa formulário de alteração de telefone
  phoneInitialize(){
    this.configurationPhoneForm = this.formBuilder.group({
      phone: ['', [Validators.required]]
    })
  }

  // =========================================================================
  // FORMULÁRIOS - ENVIO DE DADOS
  // =========================================================================

  // Altera a senha do usuário
  changePassword() {
    if (this.configurationPasswordForm.value.password !== this.configurationPasswordForm.value.confirmPassword) {
      this.snackBar.open(
        'Passwords do not match',
        '', {
          duration:3000,
          panelClass: ['snackbar-danger']
        })
      return;
    }

    this.service.updatePassword(
      {
        password: this.configurationPasswordForm.value.password,
        oldPassword: this.configurationPasswordForm.value.oldPassword
      }).subscribe({
      next: () => {
        this.snackBar.open(
          'Password changed successfully',
          '', {
            duration:3000,
            panelClass: ['snackbar-success']
          })
        this.configurationPasswordForm.reset();
        this.getUser();
        this.updatePassword = !this.updatePassword;
      }
    });
  }

  // Altera o telefone do usuário
  changePhone() {
    this.service.updatePhone({phone: this.configurationPhoneForm.value.phone}).subscribe({
      next: () => {
        this.snackBar.open(
          'Phone changed successfully',
          '', {
            duration:3000,
            panelClass: ['snackbar-success']
          })
        this.configurationPhoneForm.reset();
        this.getUser();
        this.updatePhone = !this.updatePhone;
      }
    });
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE NOTIFICAÇÕES
  // =========================================================================

  // Busca todas as notificações do usuário
  allNotification() {
    this.service.allNotifications().subscribe({
      next: (response) => {
        this.notifications = response;
        this.visualizationNotifications();
      }
    });
  }

  // Marca notificações como visualizadas
  visualizationNotifications() {
    this.service.visualisationNotification().subscribe({
      next: () => {
        this.countNotifications();
      }
    });
  }

  // Conta notificações não visualizadas
  countNotifications() {
    this.service.countNotifications().subscribe({
      next: (response) => {
        this.notificationCount = response;
      }
    });
  }

  // Oculta notificação específica
  occultNotification(notificationId: number) {
    this.service.ocultNotification(notificationId).subscribe({
      next: () => {
        this.allNotification();
      }
    });
  }

  // =========================================================================
  // INTERFACE - CONTROLES DE MODAIS E SIDEBARS
  // =========================================================================

  // Abre modal de alteração de senha
  openUpdatePassword() {
    this.updatePassword = !this.updatePassword;
    this.updatePhone = false;
  }

  // Abre modal de alteração de telefone
  openUpdatePhone() {
    this.updatePhone = !this.updatePhone;
    this.updatePassword = false;
  }

  // Abre modal de configurações
  openModalConfigurationa() {
    this.openSidebarMenu = !this.openSidebarMenu;
    this.openModalConfiguration = !this.openModalConfiguration
    this.getUser();
  }

  // Abre modal de exclusão de conta
  openModalDeleteAccount() {
    this.deleteAccount = !this.deleteAccount;
  }

  // Abre sidebar de notificações
  openSideBarNotifications() {
    this.openSideBarNotification = !this.openSideBarNotification;
    this.allNotification();
  }

  // Fecha sidebar de notificações
  closeSideBarNotification() {
    this.openSideBarNotification = !this.openSideBarNotification;
    this.countNotifications();
  }

  // =========================================================================
  // LISTAS INTERATIVAS - HANDLERS
  // =========================================================================

  // Alterna botões da lista de configurações
  toggleButton(id: number) {
    this.configList.forEach(button => {
      if (button.id === id) {
        button.pressed = !button.pressed;
      } else {
        button.pressed = false;
      }
    });
  }

  // =========================================================================
  // NAVEGAÇÃO E REDIRECIONAMENTOS
  // =========================================================================

  // Redireciona para QR Code de autenticação
  QrCode(){
    if(this.userId){
      this.service.getUserWithId(this.userId).subscribe({
        next: (response) => {
          this.router.navigate(['my-bank.com.br/confirm-email'], { queryParams: { email: response.email }});
        }
      })
    }
  }

  // Retorna para página anterior
  return() {
    window.history.go(-1);
  }

  // =========================================================================
  // INTERFACE - TEMAS E UTILITÁRIOS
  // =========================================================================

  // Alterna entre temas claro e escuro
  toggleTheme(){
    this.isDarkTheme = !this.isDarkTheme;
    localStorage.setItem('theme', this.isDarkTheme ? 'dark' : 'light');
  }

  // Recarrega a página atual
  reload() {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(currentUrl);
    });
  }

  // =========================================================================
  // AUTENTICAÇÃO - LOGOUT
  // =========================================================================

  // Realiza logout mantendo dados locais
  logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.snackBar.open(
      'logout successfully',
      '', {
        duration:1500,
        panelClass: ['snackbar-success']
      });
    this.router.navigate(['my-bank.com.br/login']);
  }

  // Realiza logout completo removendo todos os dados
  getOutAll() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    this.snackBar.open(
      'logout successfully',
      '', {
      duration:1500,
      panelClass: ['snackbar-success']
    });
    this.router.navigate(['my-bank.com.br/login'])
  }
}
