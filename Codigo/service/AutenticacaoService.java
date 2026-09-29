package Codigo.service;

import Codigo.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class AutenticacaoService {
    private final List<Usuario> usuarios;

    public AutenticacaoService() {
        this.usuarios = new ArrayList<>();
    }

    /** Compartilha a mesma lista de usuarios do repositorio central. */
    public AutenticacaoService(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario autenticar(String login, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.autenticar(login, senha)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean validarCredenciais(String login, String senha) {
        return autenticar(login, senha) != null;
    }

    public void cadastrarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuarios.add(usuario);
        }
    }

    public boolean existeLogin(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }
}
