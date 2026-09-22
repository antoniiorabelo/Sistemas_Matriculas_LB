package Codigo;

public class Disciplina {
    private String codigo;
    private String nome;
    private int numeroCreditos;
    private boolean ativa;

    public Disciplina(String codigo, String nome, int numeroCreditos) {
        this.codigo = codigo;
        this.nome = nome;
        this.numeroCreditos = numeroCreditos;
        this.ativa = true;
    }

    public void inativar() {
        ativa = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getNumeroCreditos() {
        return numeroCreditos;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
