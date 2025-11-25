import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Notifications } from '../../dtos/notification/Notifications';
import { ResponseUser } from '../../dtos/user/ResponseUser';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Payments } from '../../dtos/payment/Payments';
import { forkJoin } from 'rxjs';
import { AllPayments } from '../../dtos/payment/allPayments';
import { PaymentService } from '../../service/payment/payment.service';

@Component({
  selector: 'app-payment',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent {

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS DO USUÁRIO
  // =========================================================================
  userId?: string;
  admin = false;
  user: ResponseUser = {
    cpf: '', fullName: '', email: '', password: '', phone: '', date: ''
  };
  clientIsVerified = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - PAGAMENTOS
  // =========================================================================
  paymentForm!: FormGroup;
  creditPaymentForm!: FormGroup;
  confirmPayment = false;
  confirmCreditPayment = false;

  sendPayments: Payments[] = [];
  receivePayments: Payments[] = [];
  payments: Payments[] = [];
  allPayments: AllPayments[] = [];

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - CRÉDITO E SALDO
  // =========================================================================
  payPix = true;
  payDebit = false;
  limitOfCredit: any;
  isChecked = false;
  salary = 0;
  money = 0;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - INTERFACE
  // =========================================================================
  isDarkTheme = false;
  openSidebarMenu = false;
  notificationCount: number = 0;
  openSideBarNotification = false;
  notifications: Notifications[] = [];

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - PAGINAÇÃO E FILTROS
  // =========================================================================
  currentPage = 1;
  itemsPerPage = 4;
  searchTerm = '';

  // =========================================================================
  // LISTAS DE BOTÕES INTERATIVOS
  // =========================================================================
  buttons = [
    {id: 1, name: "Send Money", pressed: true},
    {id: 2, name: "Pay bill", pressed: false},
    {id: 3, name: "Credit card", pressed: false},
    {id: 4, name: "History of payments", pressed: false}
  ];

  buttonsOfPayment = [
    {id: 1, name: "Pix", pressed: true},
    {id: 2, name: "Credit", pressed: false}
  ];

  constructor(
    private router: Router,
    private service: PaymentService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  // =========================================================================
  // CICLO DE VIDA - ngOnInit
  // =========================================================================
  ngOnInit(): void {
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
    this.initializePaymentForm();
    this.initializeCreditPaymentForm();
    this.getWallet();
    this.getLimitOfCredit();
    this.getUserAdm();

    this.checkVerificationClient();
    this.countNotifications();

    this.getCombinedPayments();
    this.getLimitOfCreditSalary();
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE USUÁRIO
  // =========================================================================

  // Obtém dados do usuário pelo ID
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

  // Verifica status de verificação do usuário
  checkVerificationClient() {
    this.service.checkUserVerification().subscribe({
      next: (response) => {
        if (response === true){
          this.clientIsVerified = true;
        } else {
          this.clientIsVerified = false;
        }
      }
    })
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE CARTEIRA E CRÉDITO
  // =========================================================================

  // Obtém o saldo da carteira
  getWallet() {
    if (this.userId) {
      this.service.getWallet().subscribe( response =>
        this.money = response.money
      );
    }
  }

  // Obtém o limite de crédito disponível
  getLimitOfCredit(){
    this.service.getLimitOfCredit().subscribe( response => {
      this.limitOfCredit = response
      console.log(response);
    })
  }

  // Obtém o limite de crédito baseado no salário
  getLimitOfCreditSalary(){
    this.service.getLimitCreditSalary().subscribe( response => {
      if (response === null){
        this.salary = 0
      }
      this.salary = (response * 0.3)
    })
  }

  // =========================================================================
  // SERVICOS - HISTÓRICO DE PAGAMENTOS
  // =========================================================================

  // Combina e processa todos os pagamentos (enviados e recebidos)
  getCombinedPayments() {
    if (this.userId) {
      forkJoin([
        this.service.getAllSendPayments(),
        this.service.getAllReceivePayments()
      ]).subscribe(([sendPayments, receivePayments]) => {

        const combined = [...sendPayments, ...receivePayments];
        combined.sort((a, b) => new Date(b.timeStamp).getTime() - new Date(a.timeStamp).getTime());

        const userRequests = combined.map(p => {
          const otherUserId = p.sendOrReceive === 'SEND' ? p.userReceive : p.userSend;
          return this.service.getUserFullName(otherUserId);
        });

        forkJoin(userRequests).subscribe(fullNames => {
          this.allPayments = combined.map((payment, i) => ({
            ...payment,
            fullName: fullNames[i],
          }));
        });
      });
    }
  }

  // Obtém pagamentos enviados
  getAllSendPayments() {
    if (this.userId) {
      this.service.getAllSendPayments().subscribe(
        response => {
          this.sendPayments = response;
        }
      );
    }
  }

  // Obtém pagamentos recebidos
  getAllReceivePayments() {
    if (this.userId) {
      this.service.getAllReceivePayments().subscribe(
        response => {
          if (response.map(payment => payment.userReceive == this.userId)) {
            this.receivePayments = response;
          }
        }
      );
    }
  }

  // Obtém nome completo do usuário pelo ID
  getFullName(userId: string) {
    this.service.getUserFullName(userId).subscribe(
      response => {
        return response;
      }
    );
  }

  // =========================================================================
  // FORMULÁRIOS - INICIALIZAÇÃO
  // =========================================================================

  // Inicializa formulário de pagamento
  initializePaymentForm() {
    this.paymentForm = this.formBuilder.group({
      money: [null, Validators.required],
      key: ['', Validators.required],
      pixOrCredit: ['', Validators.required]
    });
  }

  // Inicializa formulário de pagamento com crédito
  initializeCreditPaymentForm() {
    this.creditPaymentForm = this.formBuilder.group({
      money: [Validators.required]
    });
  }

  // =========================================================================
  // FORMULÁRIOS - ENVIO DE PAGAMENTOS
  // =========================================================================

  // Processa pagamento via PIX ou Débito
  payment() {
    if (this.userId) {
      this.buttonsOfPayment.forEach(button => {
        if (button.id === 1 && button.pressed) {
          this.paymentForm.get('pixOrCredit')?.setValue('PIX');
        } else if (button.id === 2 && button.pressed) {
          this.paymentForm.get('pixOrCredit')?.setValue('CREDIT');
        }
      });

      this.service.payment(this.paymentForm.value).subscribe({
        next: (response) => {
          this.confirmPayment = !this.confirmPayment;
          this.snackBar.open('Payment sent successfully!', '', {
            duration: 4000,
            panelClass: ['snackbar-success']
          });

          setTimeout(() => {
            this.money;
            this.getWallet();
            this.getLimitOfCredit();
            this.getCombinedPayments();
          }, 1000);

          this.paymentForm.reset();
        },
        error: (err) => {
          console.log(err);
          this.snackBar.open('Erro ao realizar pagamento', '', {
            duration: 4000
          });
        }
      });
    }
  }

  // Processa pagamento com crédito
  creditPayment() {
    if (this.userId) {

      console.log(this.creditPaymentForm.value);

      this.service.creditPayment(this.creditPaymentForm.value).subscribe({
        next: (response) => {
          this.confirmCreditPayment = !this.confirmCreditPayment;

          this.snackBar.open('Payment sent successfully!', '', {
            duration: 4000,
            panelClass: ['snackbar-success']
          });

          setTimeout(() => {
            this.getWallet();
            this.getLimitOfCredit();
            this.getLimitOfCreditSalary();
          }, 1000);

          this.creditPaymentForm.reset();
        },
        error: (err) => {
          console.log(err);
          this.snackBar.open('Erro ao realizar pagamento', '', {
            duration: 4000
          });
        }
      });
    }
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
  // INTERFACE - CONTROLES DE PAGAMENTO
  // =========================================================================

  // Seleciona pagamento via PIX
  payPixClick(){
    this.payPix = true;
    this.payDebit = false;
  }

  // Seleciona pagamento via Débito
  payDebitClick(){
    this.payDebit = true;
    this.payPix = false;
  }

  // =========================================================================
  // INTERFACE - CONTROLES DE SIDEBAR
  // =========================================================================

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

  // Alterna botões principais
  toggleButton(id: number) {
    this.buttons.forEach(button => {
      if (button.id === id) {
        button.pressed = true;
      } else {
        button.pressed = false;
      }
    });
  }

  // Alterna botões de tipo de pagamento
  toggleButtonOfPayment(id: number) {
    this.buttonsOfPayment.forEach(button => {
      if (button.id === id) {
        button.pressed = true;
      } else {
        button.pressed = false;
      }
    });
  }

  // =========================================================================
  // NAVEGAÇÃO E REDIRECIONAMENTOS
  // =========================================================================

  // Redireciona para configurações
  redirectForConfiguration() {
    this.router.navigate(['my-bank.com.br/configuration'])
  }

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
  return(){
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

  // =========================================================================
  // AUTENTICAÇÃO - LOGOUT
  // =========================================================================

  // Realiza logout e recarrega a página
  logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.reload();
  }

  // =========================================================================
  // PAGINAÇÃO E FILTROS
  // =========================================================================

  // Normaliza texto para busca
  normalize(str: any) {
    return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim().toLowerCase();
  }

  // Filtra pagamentos com base no termo de busca
  get filteredPayments() {
    const searchTerm = this.normalize(this.searchTerm);

    return this.allPayments.filter(payment => {
      const matchesSearch = searchTerm === '' ||
        this.normalize(payment.fullName).includes(searchTerm) ||
        this.normalize(payment.timeStamp).includes(searchTerm);

      return matchesSearch;
    });
  }

  // Obtém pagamentos paginados
  get paginatedPayments() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredPayments.slice(start, start + this.itemsPerPage);
  }

  // Calcula total de páginas
  get totalPages(): number {
    return Math.ceil(this.filteredPayments.length / this.itemsPerPage);
  }

  // Avança para próxima página
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  // Volta para página anterior
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }
}
