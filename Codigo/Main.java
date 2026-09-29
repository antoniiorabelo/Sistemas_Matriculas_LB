package Codigo;

import Codigo.model.Aluno;
import Codigo.model.Usuario;
import Codigo.service.AutenticacaoService;

public class Main {
    public static void main(String[] args) {

        AutenticacaoService autenticacaoService = new AutenticacaoService();

        Aluno aluno = new Aluno(
                "1",
                "João Pedro",
                "joao",
                "1234",
                "2026001"
        );

        autenticacaoService.cadastrarUsuario(aluno);

        Usuario usuario = autenticacaoService.autenticar("joao", "1234");

        if (usuario != null) {
            System.out.println("Login realizado com sucesso!");
            System.out.println("Usuário: " + usuario.getNome());
        } else {
            System.out.println("Login ou senha inválidos.");
        }
    }
}