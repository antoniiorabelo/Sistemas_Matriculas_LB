package Codigo;

public class Secretaria extends Usuario {
    private String registro;

    public Secretaria(String id, String nome, String login, String senha, String registro) {
        super(id, nome, login, senha);
        this.registro = registro;
    }

    public void abrirPeriodoMatricula(PeriodoMatricula periodo) {
        if (periodo != null) {
            periodo.abrir();
        }
    }

    public void encerrarPeriodoMatricula(PeriodoMatricula periodo) {
        if (periodo != null) {
            periodo.encerrar();
        }
    }

    public CurriculoSemestral gerarCurriculo(Curso curso, Semestre semestre) {
        return new CurriculoService().criarCurriculo(curso, semestre);
    }

    public String getRegistro() {
        return registro;
    }
}
