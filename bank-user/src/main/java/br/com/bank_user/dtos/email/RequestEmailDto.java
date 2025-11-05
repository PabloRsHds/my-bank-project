package br.com.bank_user.dtos.email;

//Para a verificação do usuario, ele vai digitar o código e o -email
//será pego do parâmetro da url
public record RequestEmailDto(
        String email,
        String code) {
}
