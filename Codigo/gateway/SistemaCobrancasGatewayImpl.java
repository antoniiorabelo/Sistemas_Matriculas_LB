package Codigo.gateway;

import Codigo.model.Aluno;
import Codigo.model.Matricula;
import Codigo.model.Semestre;
import java.util.List;

/**
 * Implementacao simulada do Sistema de Cobrancas (fora do escopo deste projeto).
 * Como o calculo e o recebimento do pagamento nao fazem parte do sistema,
 * esta classe apenas simula a integracao via console, sempre retornando sucesso.
 */
public class SistemaCobrancasGatewayImpl implements SistemaCobrancasGateway {

    @Override
    public boolean notificarMatricula(Aluno aluno, Semestre semestre, List<Matricula> matriculas) {
        System.out.println("[Sistema de Cobrancas] Recebida inclusao de matricula(s) de "
                + aluno.getNome() + " (" + aluno.getMatricula() + ") no semestre " + semestre
                + " - " + matriculas.size() + " disciplina(s).");
        return true;
    }

    @Override
    public boolean notificarCancelamento(Aluno aluno, Semestre semestre, Matricula matricula) {
        System.out.println("[Sistema de Cobrancas] Recebido cancelamento de "
                + aluno.getNome() + " (" + aluno.getMatricula() + ") no semestre " + semestre
                + " - disciplina " + matricula.getOferta().getDisciplina().getNome() + ".");
        return true;
    }
}
