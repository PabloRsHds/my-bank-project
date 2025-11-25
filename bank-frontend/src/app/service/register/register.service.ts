import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, catchError, throwError } from 'rxjs';
import { User } from '../../validators/User';


@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http:HttpClient) { }

  //Serviço de registro de usuário.
  //O { [key: string]: string } diz que o retorno do post vai ser um objeto com uma chave string e um valor string,
  //no caso um MAP do backend.
  register(user: User): Observable<string> {
    return this.http.post<{ [key: string]: string }>('http://localhost:8080/api/register', user).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0]; // Pega a primeira chave do objeto
        return response[firstKey]; // Retorna o valor da primeira chave
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg:string; // Variável para armazenar o erro

        // err.error é um objeto: { "Bad request": "This cpf already cadastred" }
        if (err.error && typeof err.error === 'object') { // Se err.error for um objeto
          const keys = Object.keys(err.error); // Pega as chaves do objeto
          if (keys.length > 0) { // Se houver pelo menos uma chave
            errorMsg = err.error[keys[0]]; // <- pega "This cpf already cadastred"
          }
        }
        return throwError(() => new Error(errorMsg)); // Retorna o erro
      })
    );
  }
}
