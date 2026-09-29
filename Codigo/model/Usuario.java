package Codigo.model;

import java.util.Objects;

public abstract class Usuario {
    private String id;
    private String nome;
    private String login;
    private String senha;
    private boolean ativo;

    protected Usuario(String id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.ativo = true;
    }

    public boolean autenticar(String login, String senha) {
        return ativo
                && Objects.equals(this.login, login)
                && Objects.equals(this.senha, senha);
    }

    public boolean alterarSenha(String senhaAtual, String novaSenha) {
        if (!Objects.equals(this.senha, senhaAtual)
                || novaSenha == null
                || novaSenha.trim().isEmpty()) {
            return false;
        }
        this.senha = novaSenha;
        return true;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    /** Usado apenas pela camada de persistencia para gravar o usuario em arquivo. */
    public String getSenha() {
        return senha;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
