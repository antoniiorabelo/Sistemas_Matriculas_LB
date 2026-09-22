package Codigo;

import java.time.LocalDateTime;

public class Matricula {
    private String id;
    private LocalDateTime dataMatricula;
    private LocalDateTime dataCancelamento;
    private StatusMatricula status;
    private Aluno aluno;
    private OfertaDisciplina oferta;

    public Matricula(String id, Aluno aluno, OfertaDisciplina oferta) {
        this.id = id;
        this.aluno = aluno;
        this.oferta = oferta;
    }

    public void confirmar() {
        status = StatusMatricula.CONFIRMADA;
        dataMatricula = LocalDateTime.now();
    }

    public void cancelar() {
        status = StatusMatricula.CANCELADA;
        dataCancelamento = LocalDateTime.now();
    }

    public boolean estaAtiva() {
        return status == StatusMatricula.CONFIRMADA;
    }

    public boolean pertenceAoSemestre(Semestre semestre) {
        return oferta != null
                && oferta.getCurriculo() != null
                && oferta.getCurriculo().getSemestre() == semestre;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public OfertaDisciplina getOferta() {
        return oferta;
    }

    public StatusMatricula getStatus() {
        return status;
    }
}
