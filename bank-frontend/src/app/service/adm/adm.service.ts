import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResponseDocuments } from '../../dtos/adm/ResponseDocuments';
import { CreditDocuments } from '../../dtos/document/CreditDocument';
import { ResponseReports } from '../../dtos/report/ResponseReports';
import { ResponseAllUsers } from '../../dtos/user/ResponseAllUsers';

@Injectable({
  providedIn: 'root'
})
export class AdmService {

  constructor(private http:HttpClient) { }

  /**
   * Obtém todos os documentos de crédito para análise
   * @returns Observable<CreditDocuments[]> - Lista de documentos de crédito
   * @endpoint GET http://localhost:8081/api/credit-documents
   */
  creditDocuments(): Observable<CreditDocuments[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<CreditDocuments[]>('http://localhost:8081/api/credit-documents', { headers });
  }

  /**
   * Aprova um documento de crédito
   * @param creditDocumentId - ID do documento de crédito a ser aprovado
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8081/api/approve-credit-document
   */
  approveCreditDocument(creditDocumentId: number): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8081/api/approve-credit-document', {creditDocumentId : creditDocumentId}, { headers });
  }

  /**
   * Rejeita um documento de crédito
   * @param creditDocumentId - ID do documento de crédito a ser rejeitado
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8081/api/reject-credit-document
   */
  rejectCreditDocument(creditDocumentId: number): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8081/api/reject-credit-document',{creditDocumentId : creditDocumentId}, { headers });
  }

  /**
   * Obtém todos os documentos de identificação para análise
   * @returns Observable<ResponseDocuments[]> - Lista de documentos
   * @endpoint GET http://localhost:8081/api/documents
   */
  documents(): Observable<ResponseDocuments[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<ResponseDocuments[]>('http://localhost:8081/api/documents', { headers });
  }

  /**
   * Aprova um documento de identificação
   * @param documentId - ID do documento a ser aprovado
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8081/api/approve-document
   */
  approveDocument(documentId: number): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8081/api/approve-document', {documentId : documentId}, { headers });
  }

  /**
   * Rejeita um documento de identificação
   * @param documentId - ID do documento a ser rejeitado
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8081/api/reject-document
   */
  rejectDocument(documentId: number): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8081/api/reject-document',{documentId : documentId}, { headers });
  }

  /**
   * Obtém todos os relatórios de roubo
   * @returns Observable<ResponseReports[]> - Lista de relatórios
   * @endpoint GET http://localhost:8082/api/reports
   */
  reports(): Observable<ResponseReports[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<ResponseReports[]>('http://localhost:8082/api/reports', { headers });
  }

  /**
   * Obtém todos os usuários do sistema (apenas administradores)
   * @returns Observable<ResponseAllUsers[]> - Lista de usuários
   * @endpoint GET http://localhost:8080/api/adm/get-all-users
   */
  viewAllUsers(): Observable<ResponseAllUsers[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<ResponseAllUsers[]>('http://localhost:8080/api/adm/get-all-users', { headers });
  }

  /**
   * Ativa um usuário pelo CPF
   * @param cpf - CPF do usuário a ser ativado
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8080/api/adm/active-user
   */
  activeUser(cpf: string): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8080/api/adm/active-user', {cpf : cpf}, { headers });
  }

  /**
   * Bloqueia um usuário pelo CPF
   * @param cpf - CPF do usuário a ser bloqueado
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8080/api/adm/block-user
   */
  blockUser(cpf: string): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8080/api/adm/block-user', {cpf : cpf}, { headers });
  }
}
