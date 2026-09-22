package Codigo;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {
    private String registro;
    private final List<OfertaDisciplina> ofertas = new ArrayList<>();

    public Professor(String id, String nome, String login, String senha, String registro) {
        super(id, nome, login, senha);
        this.registro = registro;
    }

    public List<OfertaDisciplina> consultarOfertas() {
        return new ArrayList<>(ofertas);
    }

    public List<Aluno> consultarAlunosMatriculados(OfertaDisciplina oferta) {
        if (oferta == null || !ofertas.contains(oferta)) {
            return new ArrayList<>();
        }
        return oferta.consultarAlunosMatriculados();
    }

    public void adicionarOferta(OfertaDisciplina oferta) {
        if (oferta != null && !ofertas.contains(oferta)) {
            ofertas.add(oferta);
        }
    }

    public String getRegistro() {
        return registro;
    }
}
