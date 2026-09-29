package Codigo.model;

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

    public void reativar() {
        ativa = true;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disciplina)) return false;
        Disciplina that = (Disciplina) o;
        return codigo != null && codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo == null ? 0 : codigo.hashCode();
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + (ativa ? "" : " (inativa)");
    }
}
