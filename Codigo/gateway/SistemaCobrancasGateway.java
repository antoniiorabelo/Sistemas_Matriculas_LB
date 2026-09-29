package Codigo.gateway;

import Codigo.model.Aluno;
import Codigo.model.Matricula;
import Codigo.model.Semestre;
import java.util.List;

public interface SistemaCobrancasGateway {
    boolean notificarMatricula(Aluno aluno, Semestre semestre, List<Matricula> matriculas);
    boolean notificarCancelamento(Aluno aluno, Semestre semestre, Matricula matricula);
}
