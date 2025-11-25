import { CreditDocuments } from './../../dtos/document/CreditDocument';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AdmService } from '../../service/adm/adm.service';

@Component({
  selector: 'app-view-credit-documents',
  imports: [CommonModule, FormsModule],
  templateUrl: './view-credit-documents.component.html',
  styleUrl: './view-credit-documents.component.css'
})
export class ViewCreditDocumentsComponent {

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - INTERFACE
  // =========================================================================
  isDarkTheme = false;
  openSidebarMenu = false;
  loadingDocuments = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - FILTROS E PESQUISA
  // =========================================================================
  searchTerm: string = '';
  statusFilter: string = 'all';

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS
  // =========================================================================
  creditDocuments: CreditDocuments[] = [];

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - PAGINAÇÃO
  // =========================================================================
  currentPage = 1;
  itemsPerPage = 5;

  constructor(
    private service: AdmService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  // =========================================================================
  // CICLO DE VIDA - ngOnInit
  // =========================================================================
  ngOnInit() {
    if (typeof window !== 'undefined') {
      const theme = localStorage.getItem('theme');
      if (theme === 'dark') {
        this.isDarkTheme = true;
      } else {
        this.isDarkTheme = false;
      }
    }

    this.getAllCreditDocuments();
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE DOCUMENTOS DE CRÉDITO
  // =========================================================================

  // Obtém todos os documentos de crédito para análise
  getAllCreditDocuments() {
    if (this.creditDocuments.length === 0) {
      this.loadingDocuments = true;
      console.log("passou aqui");
      console.log(this.loadingDocuments);

      setTimeout(() => {
        this.service.creditDocuments().subscribe({
          next: (response) => {
            this.creditDocuments = response;
            this.loadingDocuments = false;
          }
        });
      }, 500);
    } else {
      this.service.creditDocuments().subscribe({
        next: (response) => {
          this.creditDocuments = response;
        }
      });
    }
  }

  // Aprova um documento de crédito
  approveCreditDocument(creditDocumentId: number) {
    this.service.approveCreditDocument(creditDocumentId).subscribe({
      next: (response) => {
        this.getAllCreditDocuments();
        this.snackBar.open(
          'Document approved successfully!',
          '',
          { duration: 3000, panelClass: ['snackbar-success'] }
        );
      },
    });
  }

  // Rejeita um documento de crédito
  rejectCreditDocument(creditDocumentId: number) {
    this.service.rejectCreditDocument(creditDocumentId).subscribe({
      next: (response) => {
        this.getAllCreditDocuments();
        this.snackBar.open(
          'Document successfully rejected!',
          '',
          { duration: 3000, panelClass: ['snackbar-success'] }
        );
      },
    });
  }

  // =========================================================================
  // NAVEGAÇÃO E REDIRECIONAMENTOS
  // =========================================================================

  // Redireciona para página de configurações
  redirectForConfiguration() {
    this.router.navigate(['my-bank.com.br/configuration']);
  }

  // Retorna para página anterior
  return() {
    window.history.go(-1);
  }

  // =========================================================================
  // INTERFACE - CONTROLES DE TEMA E SIDEBAR
  // =========================================================================

  // Alterna entre temas claro e escuro
  toggleTheme() {
    this.isDarkTheme = !this.isDarkTheme;
    localStorage.setItem('theme', this.isDarkTheme ? 'dark' : 'light');
  }

  // Alterna a visibilidade da sidebar
  toggleSidebar() {
    this.openSidebarMenu = !this.openSidebarMenu;
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

  // Filtra documentos com base no termo de busca e status
  get filteredDocuments() {
    return this.creditDocuments.filter(doc => {
      const matchesSearch = this.searchTerm.trim() === '' ||
        doc.occupation.includes(this.searchTerm) ||
        doc.date.includes(this.searchTerm) ||
        doc.cpf.includes(this.searchTerm) ||
        doc.creditDocumentId.toString().includes(this.searchTerm);

      const matchesStatus = this.statusFilter === 'all' || doc.status === this.statusFilter.toUpperCase();

      return matchesSearch && matchesStatus;
    });
  }

  // Obtém documentos paginados
  get paginatedDocuments() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredDocuments.slice(start, start + this.itemsPerPage);
  }

  // Calcula o total de páginas
  get totalPages(): number {
    return Math.ceil(this.filteredDocuments.length / this.itemsPerPage);
  }

  // Avança para a próxima página
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  // Volta para a página anterior
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }
}
