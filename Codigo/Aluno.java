package Codigo;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
    private String matricula;
    private final List<Matricula> matriculas = new ArrayList<>();

    public Aluno(String id, String nome, String login, String senha, String matricula) {
        super(id, nome, login, senha);
        this.matricula = matricula;
    }

    public List<OfertaDisciplina> consultarOfertas(Semestre semestre) {
        // Stub do LB01S02: a busca será ligada ao currículo semestral nas próximas sprints.
        return new ArrayList<>();
    }

    public List<Matricula> consultarMatriculas(Semestre semestre) {
        List<Matricula> resultado = new ArrayList<>();
        for (Matricula matriculaRealizada : matriculas) {
            if (matriculaRealizada.pertenceAoSemestre(semestre)) {
                resultado.add(matriculaRealizada);
            }
        }
        return resultado;
    }

    public void adicionarMatricula(Matricula matriculaRealizada) {
        matriculas.add(matriculaRealizada);
    }

    public String getMatricula() {
        return matricula;
    }
}


