import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms'
import { NgxMaskDirective, provideNgxMask } from "ngx-mask";
import { MatSelectModule } from '@angular/material/select';
import { cpfValidator } from '../../validators/cpfValidator';
import { Notifications } from '../../dtos/notification/Notifications';
import { ResponseUserCard } from '../../dtos/card/ResponseUserCard';
import { ResponseUser } from '../../dtos/user/ResponseUser';
import { dateValidator } from '../../validators/dateValidator';
import { AdmClientService } from '../../service/adm-client/adm-client.service';

@Component({
  selector: 'app-client',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgxMaskDirective,
    MatSelectModule

],
  providers: [provideNgxMask({dropSpecialCharacters:false})],
  templateUrl: './client.component.html',
  styleUrl: './client.component.css'
})
export class ClientComponent {

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS DO USUÁRIO
  // =========================================================================
  userId?: string;

  user: ResponseUser = {
    cpf: '',
    fullName: '',
    email: '',
    password: '',
    phone: '',
    date: ''
  }

  admin = false;
  showConfigs = false;
  isDarkTheme = false;
  clientIsVerified = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - CARTÃO
  // =========================================================================
  userCard: ResponseUserCard = {
    fullName: '',
    cardNumber: '',
    expirationDate: '',
    cardCvv: '',
    limitCredit: 0,
    typeOfCard: ''
  }

  notHaveCard = false;
  haveAnApprovedCard = false;
  haveAnCanceledCard = false;
  haveAnBlockedCard = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - STATUS DE DOCUMENTOS
  // =========================================================================
  documentSend = false;
  documentPending = false;
  documentApproved = false;
  documentRejected = false;

  creditDocumentSend = false;
  creditDocumentPending = false;
  creditDocumentApproved = false;
  creditDocumentRejected = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - NOTIFICAÇÕES
  // =========================================================================
  notifications: Notifications[] = [];
  notificationCount: number = 0;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - EMPRÉSTIMOS
  // =========================================================================
  monthlyInstallment = '';
  totalPayment = '';
  simulationResult = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - MODAIS E SIDEBARS
  // =========================================================================
  openModalSendDocument = false;
  openViewDocuments = false;
  openModalDocumentsForAnalysis = false;
  openModalRobbedMe = false;
  openModalViewCard = false;
  openModalLoan = false;
  openModalCreditCard = false;
  openModalConfiguration = false;

  openConfigurationPassword = false;
  openConfigurationPhone = false;

  openSidebarMenu = false;
  openSideBarNotification = false;
  openViewMyCard = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - FORMULÁRIOS
  // =========================================================================
  cardAnalysisForm!: FormGroup;
  creditDocumentAnalysisForm!: FormGroup;
  reportTheftForm!: FormGroup;
  configurationPasswordForm!: FormGroup;
  configurationPhoneForm!: FormGroup;
  loanForm!: FormGroup;

  // =========================================================================
  // CARDS INTERATIVOS - USUÁRIO COMUM
  // =========================================================================
  cards = [
    { title: 'Request your card', description: 'Submit your documents for review or check document status'},
    { title: `I've been robbed`, description: 'Step by step in case of theft'},
    { title: 'View your card', description: 'View your card' },
    { title: 'Apply for a loan', description: 'Request a loan application from the bank'},
  ];

  // =========================================================================
  // CARDS INTERATIVOS - ADMINISTRADOR
  // =========================================================================
  cardsAdm = [
    { title: 'View Documents', description: 'View'},
    { title: 'View Reports', description: 'Reports'},
    { title: 'View all users', description: 'all users'},
    { title: 'This function is blocked', description: 'This function will be implemented in later updates'},
  ];

  constructor(
    private service: AdmClientService,
    private router: Router,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder) {}

  // =========================================================================
  // ngOnInit
  // =========================================================================
  ngOnInit(){
    if (typeof window !== 'undefined') {
      this.userId = localStorage.getItem('userId') ?? undefined;
      this.getUserAdm();

      const theme = localStorage.getItem('theme');
      if (theme === 'dark') {
        this.isDarkTheme = true;
      } else {
        this.isDarkTheme = false;
      }
    }

    this.checkVerificationClient();
    this.countNotifications();
    this.verifyIfUserHasCardAndYourStatus();
    this.checkDocumentStatus();
    this.checkCreditDocumentStatus();

    this.cardInitialize();
    this.initializeLoanForm();
    this.reportInitialize();
    this.creditDocumentInitialize();

    this.reLoadInicialize();
  }

  // =========================================================================
  // SERVIÇOS - GERENCIAMENTO DE USUÁRIO
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
  // SERVIÇOS - GERENCIAMENTO DE CARTÕES
  // =========================================================================

  // Verifica se o usuário possui cartão e seu status
  verifyIfUserHasCardAndYourStatus(){
    if (this.userId) {
      this.service.verifyIfUserHasCardAndYourStatus().subscribe({
        next: (response) => {
          if (response === "EMPTY") {
            this.notHaveCard = true;
            return;
          }
          else if (response === "APPROVED") {
            this.haveAnApprovedCard = true;
            return;
          }
          else if (response === "CANCELED") {
            this.haveAnCanceledCard = true;
            return;
          }
          else if (response === "BLOCKED") {
            this.haveAnBlockedCard = true;
            return;
          }
        }
      })
    }
  }

  // Obtém dados do cartão do usuário
  getUserCard(){
    if (this.userId) {
      this.service.getUserCard().subscribe({
        next: (response) => {
          this.userCard = response
        }
      })
    }
  }

  // Abre modal para visualizar cartão com validações
  openViewCard(){
    this.getUserCard();

    if (this.notHaveCard === true) {
      this.snackBar.open(
        "You don't have a card",
        '', {
          duration:3000,
          panelClass: ['snackbar-danger']
        })
      return;
    }
    else if (this.haveAnApprovedCard === true || this.haveAnBlockedCard === true) {
      this.openModalViewCard = !this.openModalViewCard;
      return;
    }
    else if (this.haveAnCanceledCard === true) {
      this.snackBar.open(
        'Your card was canceled',
        '', {
          duration:3000,
          panelClass: ['snackbar-danger']
        })
      return;
    }
  }

  // Bloqueia/desbloqueia o cartão
  blockCard() {
    this.service.blockCard().subscribe({
      next: (response) => {
        if (this.haveAnApprovedCard === true) {
          this.snackBar.open(
            'Your card was blocked',
            '', {
              duration:1000,
              panelClass: ['snackbar-danger']
            }),
            setTimeout (() => {
              localStorage.setItem('reLoadOpenViewCard', 'true');
              this.reload();
            }, 500);
          return;
        } else if (this.haveAnBlockedCard === true) {
          this.snackBar.open(
            'Your card has been unlocked',
            '', {
              duration:1000,
              panelClass: ['snackbar-success']
            }),
            setTimeout (() => {
              localStorage.setItem('reLoadOpenViewCard', 'true');
              this.reload();
            }, 500);
          return;
        }
      }
    })
  }

  // =========================================================================
  // SERVIÇOS - GERENCIAMENTO DE DOCUMENTOS
  // =========================================================================

  // Verifica status dos documentos de identificação
  checkDocumentStatus(){
    if (this.userId) {
      this.service.checkDocumentStatus().subscribe({
        next: (response) => {
          if (response == "SEND") {
            this.documentSend = true;
          } else if (response == "PENDING") {
            this.documentPending = true;
          } else if (response == "APPROVED") {
            this.documentApproved = true;
          } else if (response == "REJECTED") {
            this.documentRejected = true;
          }
        }
      })
    }
  }

  // Verifica status dos documentos de crédito
  checkCreditDocumentStatus() {
    if (this.userId) {
      this.service.checkCreditDocumentStatus().subscribe({
        next: (response) => {
          if (response == "SEND") {
            this.creditDocumentSend = true;
          } else if (response == "PENDING") {
            this.creditDocumentPending = true;
          } else if (response == "APPROVED") {
            this.creditDocumentApproved = true;
          } else if (response == "REJECTED") {
            this.creditDocumentRejected = true;
          }
        }
      })
    }
  }

  // Manipula clique em documentos com base no status
  handleDocumentClick(){
    if (this.documentSend === true) {
      this.openModalDocumentsForAnalysis = !this.openModalDocumentsForAnalysis;
      this.openModalSendDocument = false;
      return;
    }
    if (this.documentPending === true) {
      this.snackBar.open(
        'Your documents are being analyzed',
        '', {
          duration:3000,
          panelClass: ['snackbar-pending']
        })
      return;
    }
    else if (this.documentApproved === true) {
      this.snackBar.open(
        'Your documents are approved',
        '', {
          duration:3000,
          panelClass: ['snackbar-success']
        })
      return;
    }
    else if (this.documentRejected === true) {
      this.snackBar.open(
        'Your documents are rejected',
        '', {
          duration:3000,
          panelClass: ['snackbar-danger']
        })
      return;
    }
    this.openModalDocumentsForAnalysis = !this.openModalDocumentsForAnalysis;
  }

  // Abre modal de cartão de crédito com base no status
  openModalCreditCardStatus() {
    if (this.creditDocumentSend === true) {
      this.openModalViewCard = !this.openModalViewCard;
      this.openModalCreditCard = !this.openModalCreditCard;
      return;
    }
    else if (this.creditDocumentPending === true) {
      this.snackBar.open(
        'Your documents are being analyzed',
        '', {
          duration:3000,
          panelClass: ['snackbar-pending']
        })
      return;
    }
    else if (this.creditDocumentApproved === true) {
      this.snackBar.open(
        'Your credit limit is approved',
        '', {
          duration:3000,
          panelClass: ['snackbar-success']
        })
      return;
    }
    else if (this.creditDocumentRejected === true) {
      this.snackBar.open(
        'Your credit limit is rejected',
        '', {
          duration:3000,
          panelClass: ['snackbar-danger']
        })
      return;
    }
  }

  // =========================================================================
  // FORMULÁRIOS - INICIALIZAÇÃO
  // =========================================================================

  // Inicializa formulário de análise de documentos
  cardInitialize(){
    this.cardAnalysisForm = this.formBuilder.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      rg: ['', [Validators.required]],
      cpf: ['', [Validators.required, cpfValidator]],
      proofOfAddress: [null, [Validators.required]],
      proofOfIncome: [null, [Validators.required]]
    })
  }

  // Inicializa formulário de análise de documentos de crédito
  creditDocumentInitialize(){
    this.creditDocumentAnalysisForm = this.formBuilder.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      cpf: ['', [Validators.required, cpfValidator]],
      date: ['', [Validators.required, dateValidator]],
      occupation : ['', [Validators.required]],
      salary : [null],
      proofOfIncome: [null, [Validators.required]]
    })
  }

  // Inicializa formulário de relatório de roubo
  reportInitialize(){
    this.reportTheftForm = this.formBuilder.group({
      dateOfTheft: [''],
      timeOfTheft: [''],
      locationOfTheft: [''],
      transactionId: [''],
      amountLost: [0],
      description: ['']
    })
  }

  // Inicializa formulário de empréstimo
  initializeLoanForm() {
    this.loanForm = this.formBuilder.group({
      value: ['', [Validators.required]],
      term: ['', [Validators.required]],
      monthlyIncome: ['', [Validators.required]]
    });
  }

  // =========================================================================
  // FORMULÁRIOS - UPLOAD DE ARQUIVOS
  // =========================================================================

  // Manipula upload de arquivos para documentos
  onFileChange(event: Event, controlName: 'proofOfAddress' | 'proofOfIncome') {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      console.log(`[${controlName}] Arquivo selecionado:`, file);
      this.cardAnalysisForm.patchValue({ [controlName]: file });
      this.cardAnalysisForm.get(controlName)?.updateValueAndValidity();
    }
  }

  // Manipula upload de arquivos para documentos de crédito
  onFileCreditChange(event: Event, controlName: 'proofOfIncome') {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      console.log(`[${controlName}] Arquivo selecionado:`, file);
      this.creditDocumentAnalysisForm.patchValue({ [controlName]: file });
      this.creditDocumentAnalysisForm.get(controlName)?.updateValueAndValidity();
    }
  }

  // =========================================================================
  // FORMULÁRIOS - ENVIO DE DADOS
  // =========================================================================

  // Envia documentos para análise
  documentsForAnalysis() {
    if (this.cardAnalysisForm.invalid) return;

    const formData = new FormData();
    const formValues = this.cardAnalysisForm.value;

    formData.append('fullName', formValues.fullName);
    formData.append('rg', formValues.rg);
    formData.append('cpf', formValues.cpf);
    formData.append('proofOfAddress', formValues.proofOfAddress as File);
    formData.append('proofOfIncome', formValues.proofOfIncome as File);

    console.log('[formData]', formData);

    this.service.documentsForAnalysis(formData).subscribe({
      next: () => {
        this.snackBar.open('Your documents have been sent for analysis', '', {
          duration: 1500,
          panelClass: ['snackbar-success']
        });

        setTimeout(() => {
          localStorage.setItem('reLoadOpenAskYourCard','true');
          this.reload();
        }, 2000);
      },
      error: (err) => console.error('Erro detalhado:', err)
    });
  }

  // Envia documentos de crédito para análise
  creditDocumentsForAnalysis() {
    if (this.creditDocumentAnalysisForm.invalid) return;

    const formData = new FormData();
    const formValues = this.creditDocumentAnalysisForm.value;

    if (this.userId) {
      formData.append('userId', this.userId);
      formData.append('fullName', formValues.fullName)
      formData.append('cpf', formValues.cpf);
      formData.append('date', formValues.date);
      formData.append('occupation', formValues.occupation);
      formData.append('salary', formValues.salary);
      formData.append('proofOfIncome', formValues.proofOfIncome);
    }

    this.service.creditDocumentsForAnalysis(formData).subscribe({
      next: () => {
        this.snackBar.open(
          'Your documents has been sent for analysis',
          '', {
          duration: 1500,
          panelClass: ['snackbar-success']
        });
        setTimeout(() => {
          localStorage.setItem('reLoadOpenViewCard','true');
          this.reload();
        }, 2000);
      },
      error: (err) => console.error(err)
    });
  }

  // Envia relatório de roubo
  reportTheft() {
    this.service.reportTheft(this.reportTheftForm.value).subscribe({
      next: (response) => {
          this.snackBar.open(
            'Report sent successfully',
            '', {
              duration:3000,
              panelClass: ['snackbar-success']
            })
          this.reportTheftForm.reset();
          this.openModalRobbedMe = !this.openModalRobbedMe;
      },
      error: (err) => {
          this.snackBar.open(
            'Unable to send report at this time',
            '', {
              duration:3000,
              panelClass: ['snackbar']
            })
      },
    })
  }

  // =========================================================================
  // EMPRÉSTIMOS - CÁLCULOS E SIMULAÇÕES
  // =========================================================================


  showSimulationResult() {
    this.simulateLoan();
    this.simulationResult = !this.simulationResult
  }

  closeSimulationResult() {
    this.simulationResult = !this.simulationResult
  }

  // Simula empréstimo
  simulateLoan() {
    const value = this.loanForm.get('value')?.value;
    const termString = this.loanForm.get('term')?.value;
    const term = parseInt(termString);
    const monthlyIncome = this.loanForm.get('monthlyIncome')?.value;

    const interestRate = 0.08;
    const pmt = (value * interestRate) / (1 - Math.pow(1 + interestRate, -term));

    this.monthlyInstallment = pmt.toFixed(2)
    this.totalPayment = (pmt * term).toFixed(2);

    if (pmt > monthlyIncome * 0.3) {
      console.log("Loan not approved (portion with commitment of more than 30% of income)");
    } else {
      console.log("Loan approved");
    }
  }

  // Aplica para empréstimo
  applyForLoan() {
    const value = this.loanForm.get('value')?.value;
    const termString = this.loanForm.get('term')?.value;
    const term = parseInt(termString);
    const monthlyIncome = this.loanForm.get('monthlyIncome')?.value;

    const interestRate = 0.08;
    const pmt = (value * interestRate) / (1 - Math.pow(1 + interestRate, -term));

    if (pmt > monthlyIncome * 0.3) {
      this.snackBar.open("Loan not approved (portion with commitment of more than 30% of income)", '',{
        duration: 4000,
        panelClass: ['snackbar-danger']
      })
    } else {
      this.snackBar.open("Loan approved ✓", '',{
        duration: 3500,
        panelClass: ['snackbar-success']
      }),
      setTimeout(() => {
        this.openModalLoan = !this.openModalLoan;
      }, 2000);
    }
  }

  // =========================================================================
  // NOTIFICAÇÕES - GERENCIAMENTO
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

  // Busca todas as notificações
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
  // NAVEGAÇÃO E REDIRECIONAMENTOS
  // ========================================================================

  // Redireciona para área de pagamento
  redirectForPaymentArea() {
    this.router.navigate(['my-bank.com.br/payment'])
  }

  // Redireciona para configurações
  redirectForConfiguration() {
    this.router.navigate(['my-bank.com.br/configuration'])
  }

  // Redireciona para visualização de documentos (admin)
  redirectForDocumentsView(){
    this.router.navigate(['my-bank.com.br/adm-works-view-documents'])
  }

  // Redireciona para visualização de documentos de crédito (admin)
  redirectForCreditDocumentsView() {
    this.router.navigate(['my-bank.com.br/adm-works-view-credit-documents'])
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

  // =========================================================================
  // INTERFACE - UTILITÁRIOS
  // =========================================================================

  // Copia texto para área de transferência
  copyText(text : any) {
    const copy = text;
    navigator.clipboard.writeText(copy);
    this.snackBar.open('Successfully copied', '',{
      duration: 1000
    });
  }

  // Alterna entre temas claro e escuro
  toggleTheme(){
    this.isDarkTheme = !this.isDarkTheme;
    localStorage.setItem('theme', this.isDarkTheme ? 'dark' : 'light');
  }

  // Fecha modal de relatório de roubo
  closeFormRobbedMe(){
    this.openModalRobbedMe = !this.openModalRobbedMe;
  }

  // Recarrega a página
  reload() {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(currentUrl);
    });
  }

  // Inicializa recarregamento de estados
  reLoadInicialize() {
    const reLoadOpenModalViewCard = localStorage.getItem('reLoadOpenViewCard');
    const reLoadOpenModalAskYourCard = localStorage.getItem('reLoadOpenAskYourCard');

    if (reLoadOpenModalViewCard === 'true') {
      this.getUserCard();
      this.openModalViewCard = true;
      localStorage.removeItem('reLoadOpenViewCard');
    }

    if (reLoadOpenModalAskYourCard === 'true') {
      this.openModalSendDocument = true;
      localStorage.removeItem('reLoadOpenAskYourCard');
    }
  }

  // =========================================================================
  // AUTENTICAÇÃO - LOGOUT
  // =========================================================================

  // Realiza logout
  logout(){
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
  };

  // Realiza logout completo
  getOutAll(){
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

  // =========================================================================
  // CARDS INTERATIVOS - HANDLERS
  // =========================================================================

  // Manipula clique nos cards principais
  handleCardClick(card: any) {
    switch (card.title) {
      case 'Request your card':
        this.openModalSendDocument = !this.openModalSendDocument
        break;
      case `I've been robbed`:
        this.openModalRobbedMe = !this.openModalRobbedMe
        break;
      case 'View your card':
        this.openViewCard();
        break;
      case 'Apply for a loan':
        this.openModalLoan = !this.openModalLoan
        break;
      default:
        break;
    }
  }

  // Manipula clique nos cards administrativos
  handleCardAdmClick(card:any){
    switch (card.title){
      case 'View Documents':
        this.openViewDocuments = !this.openViewDocuments
        break;
      case 'View Reports':
        this.router.navigate(['my-bank.com.br/adm-works-view-reports'])
        break;
      case 'View all users':
        this.router.navigate(['my-bank.com.br/adm-works-view-users'])
        break;
      case 'This function is blocked':
        break;
      default:
        break
    }
  }
}
