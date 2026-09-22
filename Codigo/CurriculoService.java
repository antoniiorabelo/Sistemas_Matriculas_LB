package Codigo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurriculoService {
    public CurriculoSemestral criarCurriculo(Curso curso, Semestre semestre) {
        return new CurriculoSemestral(UUID.randomUUID().toString(), curso, semestre);
    }

    public OfertaDisciplina adicionarOferta(
            CurriculoSemestral curriculo,
            Disciplina disciplina,
            Professor professor) {
        OfertaDisciplina oferta = new OfertaDisciplina(
                UUID.randomUUID().toString(), disciplina, professor, curriculo);
        curriculo.adicionarOferta(oferta);
        professor.adicionarOferta(oferta);
        return oferta;
    }

    public void encerrarPeriodo(Semestre semestre) {
        if (semestre != null && semestre.getPeriodoMatricula() != null) {
            semestre.getPeriodoMatricula().encerrar();
        }
    }

    public void avaliarOfertas(Semestre semestre) {
        // Stub: a coleção de currículos do semestre será persistida em sprint futura.
    }

    public List<Aluno> identificarAlunosAfetados(OfertaDisciplina oferta) {
        if (oferta != null && oferta.getStatus() == StatusOferta.CANCELADA) {
            return oferta.consultarAlunosMatriculados();
        }
        return new ArrayList<>();
    }
}
