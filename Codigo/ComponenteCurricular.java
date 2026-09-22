package Codigo;

public class ComponenteCurricular {
    private String id;
    private TipoDisciplina tipo;
    private Disciplina disciplina;

    public ComponenteCurricular(String id, Disciplina disciplina, TipoDisciplina tipo) {
        this.id = id;
        this.disciplina = disciplina;
        this.tipo = tipo;
    }

    public void alterarTipo(TipoDisciplina tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public TipoDisciplina getTipo() {
        return tipo;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }
}
