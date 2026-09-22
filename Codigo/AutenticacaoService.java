package Codigo;

import java.util.ArrayList;
import java.util.List;

public class AutenticacaoService {
    private final List<Usuario> usuarios = new ArrayList<>();

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
}
