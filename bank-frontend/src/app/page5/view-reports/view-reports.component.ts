import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ResponseReports } from '../../dtos/report/ResponseReports';
import { Router } from '@angular/router';
import { AdmService } from '../../service/adm/adm.service';

@Component({
  selector: 'app-view-reports',
  imports: [CommonModule, FormsModule],
  templateUrl: './view-reports.component.html',
  styleUrl: './view-reports.component.css'
})
export class ViewReportsComponent {

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - INTERFACE
  // =========================================================================
  isDarkTheme = false;
  openSidebarMenu = false;
  sidebarDescriptinOpen = false;
  openDescriptionId: number | null = null;
  loadingReports = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - FILTROS E PESQUISA
  // =========================================================================
  searchTerm: string = '';
  statusFilter: string = 'all';

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS
  // =========================================================================
  reports: ResponseReports[] = [];

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

    this.allReports();
  }

  // =========================================================================
  // SERVICOS - GERENCIAMENTO DE RELATÓRIOS
  // =========================================================================

  // Obtém todos os relatórios de roubo
  allReports() {
    if (this.reports.length === 0) {
      this.loadingReports = true;

      setTimeout(() => {
        this.service.reports().subscribe({
          next: (response) => {
            this.reports = response;
            this.loadingReports = false;
          }
        });
      }, 500);
    }
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

  // Filtra relatórios com base no termo de busca e status
  get filteredDocuments() {
    return this.reports.filter(report => {
      const matchesSearch = this.searchTerm.trim() === '' ||
        report.transactionId.includes(this.searchTerm);

      const matchesStatus = this.statusFilter === 'all' || report.status === this.statusFilter.toUpperCase();

      return matchesSearch && matchesStatus;
    });
  }

  // Obtém relatórios paginados
  get paginatedReports() {
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
