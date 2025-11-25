import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResponseDocuments } from '../../dtos/adm/ResponseDocuments';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AdmService } from '../../service/adm/adm.service';

@Component({
  selector: 'app-view-documents',
  imports: [CommonModule, FormsModule],
  templateUrl: './view-documents.component.html',
  styleUrl: './view-documents.component.css'
})
export class ViewDocumentsComponent {

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
  documents: ResponseDocuments[] = [];

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - PAGINAÇÃO
  // =========================================================================
  currentPage = 1;
  itemsPerPage = 5;

  constructor(
    private service: AdmService,
    private snackBar: MatSnackBar,
    private router: Router
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

    this.allDocuments();
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE DOCUMENTOS
  // =========================================================================

  // Obtém todos os documentos para análise
  allDocuments() {
    if (this.documents.length === 0) {
      this.loadingDocuments = true;
      console.log("passou aqui");
      console.log(this.loadingDocuments);

      setTimeout(() => {
        this.service.documents().subscribe({
          next: (response) => {
            this.documents = response;
            this.loadingDocuments = false;
          }
        });
      }, 500);
    } else {
      this.service.documents().subscribe({
        next: (response) => {
          this.documents = response;
        }
      });
    }
  }

  // Aprova um documento de identificação
  approveDocument(documentId: number) {
    this.service.approveDocument(documentId).subscribe({
      next: (response) => {
        this.allDocuments();
        this.snackBar.open(
          'Document approved successfully!',
          '',
          { duration: 3000, panelClass: ['snackbar-success'] }
        );
      },
    });
  }

  // Rejeita um documento de identificação
  rejectDocument(documentId: number) {
    this.service.rejectDocument(documentId).subscribe({
      next: (response) => {
        this.allDocuments();
        this.snackBar.open(
          'Document successfully rejected!',
          '',
          { duration: 3000, panelClass: ['snackbar-danger'] }
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
    return this.documents.filter(doc => {
      const matchesSearch = this.searchTerm.trim() === '' ||
        doc.rg.includes(this.searchTerm) ||
        doc.cpf.includes(this.searchTerm) ||
        doc.documentId.toString().includes(this.searchTerm);

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
