import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { ResponseAllUsers } from '../../dtos/user/ResponseAllUsers';
import { Router } from '@angular/router';
import { AdmService } from '../../service/adm/adm.service';

@Component({
  selector: 'app-view-users',
  imports: [CommonModule, FormsModule],
  templateUrl: './view-users.component.html',
  styleUrl: './view-users.component.css'
})
export class ViewUsersComponent {

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - INTERFACE
  // =========================================================================
  loadingUsers = false;
  isDarkTheme = false;
  openSidebarMenu = false;
  sibebarCards = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - FILTROS E PESQUISA
  // =========================================================================
  searchTerm: string = '';
  statusFilter: string = 'all';

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - CARDS
  // =========================================================================
  cardPhysical = false;
  cardCredit = false;

  // =========================================================================
  // PROPRIEDADES DO COMPONENTE - DADOS
  // =========================================================================
  users: ResponseAllUsers[] = [];

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
    // Verifica e aplica o tema salvo no localStorage
    if (typeof window !== 'undefined') {
      const theme = localStorage.getItem('theme');
      this.isDarkTheme = theme === 'dark';
    }

    this.allUsers();
  }

  // =========================================================================
  // SERVIÇOS - GERENCIAMENTO DE USUÁRIOS
  // =========================================================================

  /**
   * Obtém todos os usuários do sistema
   * Exibe loading durante o carregamento
   */
  allUsers() {
    if (this.users.length === 0) {
      this.loadingUsers = true;

      setTimeout(() => {
        this.service.viewAllUsers().subscribe({
          next: (response) => {
            this.users = response;
            this.loadingUsers = false;
          }
        });
      }, 500);
    }
  }

  /**
   * Ativa um usuário pelo CPF
   * @param cpf CPF do usuário a ser ativado
   */
  activeUser(cpf: string) {
    this.service.activeUser(cpf).subscribe({
      next: (response) => {
        this.allUsers(); // Recarrega a lista de usuários
      }
    });
  }

  /**
   * Bloqueia um usuário pelo CPF
   * @param cpf CPF do usuário a ser bloqueado
   */
  blockUser(cpf: string) {
    this.service.blockUser(cpf).subscribe({
      next: (response) => {
        this.allUsers(); // Recarrega a lista de usuários
      }
    });
  }

  // =========================================================================
  // NAVEGAÇÃO E REDIRECIONAMENTOS
  // =========================================================================

  /**
   * Redireciona para página de configurações
   */
  redirectForConfiguration() {
    this.router.navigate(['my-bank.com.br/configuration']);
  }

  /**
   * Retorna para página anterior
   */
  return() {
    window.history.go(-1);
  }

  // =========================================================================
  // INTERFACE - CONTROLES DE TEMA E SIDEBAR
  // =========================================================================

  /**
   * Alterna a visibilidade da sidebar de cards
   * @param number Parâmetro numérico (não utilizado no método atual)
   */
  opensidebarCards(number: number) {
    this.sibebarCards = !this.sibebarCards;
  }

  /**
   * Alterna a visibilidade da sidebar principal
   */
  toggleSidebar() {
    this.openSidebarMenu = !this.openSidebarMenu;
  }

  /**
   * Alterna entre temas claro e escuro
   */
  toggleTheme() {
    this.isDarkTheme = !this.isDarkTheme;
    localStorage.setItem('theme', this.isDarkTheme ? 'dark' : 'light');
  }

  // =========================================================================
  // AUTENTICAÇÃO - LOGOUT
  // =========================================================================

  /**
   * Realiza logout e recarrega a página
   */
  logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.reload();
  }

  // =========================================================================
  // UTILITÁRIOS - FORMATAÇÃO E NORMALIZAÇÃO
  // =========================================================================

  /**
   * Normaliza strings removendo acentos e caracteres especiais
   * @param str String a ser normalizada
   * @returns String normalizada
   */
  normalize(str: any) {
    return str.normalize("NFD")
              .replace(/[\u0300-\u036f]/g, "")
              .trim()
              .toLowerCase();
  }

  // =========================================================================
  // PAGINAÇÃO E FILTROS
  // =========================================================================

  /**
   * Filtra usuários com base no termo de busca e status
   */
  get filteredUsers() {
    const searchTerm = this.normalize(this.searchTerm);

    return this.users.filter(user => {
      const matchesSearch = searchTerm === '' ||
        this.normalize(user.cpf).includes(searchTerm) ||
        this.normalize(user.fullName).includes(searchTerm) ||
        this.normalize(user.email).includes(searchTerm) ||
        this.normalize(user.phone).includes(searchTerm);

      const matchesStatus = this.statusFilter === 'all' ||
        user.status === this.statusFilter.toUpperCase();

      return matchesSearch && matchesStatus;
    });
  }

  /**
   * Obtém usuários paginados
   */
  get paginatedUsers() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(start, start + this.itemsPerPage);
  }

  /**
   * Calcula o total de páginas
   */
  get totalPages(): number {
    return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  /**
   * Avança para a próxima página
   */
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  /**
   * Volta para a página anterior
   */
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }
}
