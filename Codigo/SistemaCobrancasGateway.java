package Codigo;

import java.util.List;

public interface SistemaCobrancasGateway {
    boolean notificarMatricula(Aluno aluno, Semestre semestre, List<Matricula> matriculas);

    boolean notificarCancelamento(Aluno aluno, Semestre semestre, Matricula matricula);
}
