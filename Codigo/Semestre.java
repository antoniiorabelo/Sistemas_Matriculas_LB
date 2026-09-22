package Codigo;

public class Semestre {
    private String id;
    private int ano;
    private int numero;
    private StatusSemestre status;
    private PeriodoMatricula periodoMatricula;

    public Semestre(String id, int ano, int numero) {
        this.id = id;
        this.ano = ano;
        this.numero = numero;
        this.status = StatusSemestre.PLANEJADO;
    }

    public void iniciar() {
        status = StatusSemestre.EM_ANDAMENTO;
    }

    public void encerrar() {
        status = StatusSemestre.ENCERRADO;
    }

    public String getId() {
        return id;
    }

    public int getAno() {
        return ano;
    }

    public int getNumero() {
        return numero;
    }

    public StatusSemestre getStatus() {
        return status;
    }

    public PeriodoMatricula getPeriodoMatricula() {
        return periodoMatricula;
    }

    public void setPeriodoMatricula(PeriodoMatricula periodoMatricula) {
        this.periodoMatricula = periodoMatricula;
    }
}
