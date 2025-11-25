import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, catchError, throwError } from 'rxjs';
import { CodeVerification } from '../../dtos/email/codeVerification';
import { ResendCodeDto } from '../../dtos/email/ResendCodeDto';

@Injectable({
  providedIn: 'root'
})
export class ConfirmCodeService {

  constructor(private http:HttpClient) { }

  checkUserEmailVerification(email:string):Observable<string>{
    return this.http.get('http://localhost:8080/api/check-email-verification', {
      params : {email},
      responseType: 'text'});
  }

  confirmCode(codeVerification:CodeVerification):Observable<string>{
    return this.http.post<{ [Key: string] :string}>('http://localhost:8080/api/verify-email',codeVerification).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0];
        return response[firstKey];
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg = 'Erro desconhecido';

        // err.error é um objeto: { "Bad request": "This cpf already cadastred" }
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]]; // <- pega "This cpf already cadastred"
          }
        }

        return throwError(() => new Error(errorMsg));
      })
    );
  }

  resendCode(request:ResendCodeDto):Observable<string>{
    return this.http.post<{ [Key: string]:string}>('http://localhost:8080/api/resend-code', request).pipe(
      map(response => {
      const firstKey = Object.keys(response)[0];
      return response[firstKey];
    }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg = 'Erro desconhecido';

        // err.error é um objeto: { "Bad request": "This cpf already cadastred" }
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]]; // <- pega "This cpf already cadastred"
          }
        }

        return throwError(() => new Error(errorMsg));
      })
    );
  }
}
