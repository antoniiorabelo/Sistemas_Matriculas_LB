package Codigo;

import Codigo.enums.TipoDisciplina;
import Codigo.gateway.SistemaCobrancasGatewayImpl;
import Codigo.model.*;
import Codigo.repository.Repositorio;
import Codigo.service.AutenticacaoService;
import Codigo.service.CadastroService;
import Codigo.service.CurriculoService;
import Codigo.service.MatriculaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Sistema de Matriculas - versao console (sem interface grafica).
 * Ponto de entrada da aplicacao: apresenta o menu de login e, de acordo com
 * o perfil autenticado (Aluno, Professor ou Secretaria), direciona para o
 * menu correspondente as suas historias de usuario (HU01 a HU11).
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Repositorio REPOSITORIO = new Repositorio();
    private static final AutenticacaoService AUTENTICACAO_SERVICE = new AutenticacaoService(REPOSITORIO.getUsuarios());
    private static final CadastroService CADASTRO_SERVICE = new CadastroService(
            REPOSITORIO.getCursos(), REPOSITORIO.getDisciplinas(),
            REPOSITORIO.getUsuarios(), AUTENTICACAO_SERVICE);
    private static final CurriculoService CURRICULO_SERVICE = new CurriculoService(REPOSITORIO.getCurriculos());
    private static final MatriculaService MATRICULA_SERVICE = new MatriculaService(
            new SistemaCobrancasGatewayImpl(), REPOSITORIO.getNotificacoes());

    public static void main(String[] args) {
        bootstrap();
        System.out.println("=======================================");
        System.out.println(" SISTEMA DE MATRICULAS - MODO TERMINAL");
        System.out.println("=======================================");

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1. Login");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");
            String opcao = SCANNER.nextLine().trim();
            switch (opcao) {
                case "1":
                    fazerLogin();
                    break;
                case "0":
                    continuar = false;
                    System.out.println("Encerrando o sistema. Ate mais!");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    /**
     * Cria uma conta de secretaria padrao na primeira execucao, para que seja
     * possivel cadastrar os demais dados (cursos, disciplinas, professores e
     * alunos) sem depender de dados pre-carregados externamente.
     */
    private static void bootstrap() {
        if (REPOSITORIO.getUsuarios().isEmpty()) {
            Secretaria secretaria = new Secretaria(
                    UUID.randomUUID().toString(), "Secretaria Academica", "secretaria", "admin123", "SEC001");
            AUTENTICACAO_SERVICE.cadastrarUsuario(secretaria);
            System.out.println("Conta inicial de secretaria criada -> login: secretaria | senha: admin123");
        }
    }

    // ==================== LOGIN / HU01 ====================

    private static void fazerLogin() {
        System.out.print("Login: ");
        String login = SCANNER.nextLine().trim();
        System.out.print("Senha: ");
        String senha = SCANNER.nextLine().trim();

        Usuario usuario = AUTENTICACAO_SERVICE.autenticar(login, senha);
        if (usuario == null) {
            System.out.println("Login ou senha invalidos.");
            return;
        }
        System.out.println("Login realizado com sucesso! Bem-vindo(a), " + usuario.getNome() + ".");

        if (usuario instanceof Secretaria) {
            menuSecretaria((Secretaria) usuario);
        } else if (usuario instanceof Professor) {
            menuProfessor((Professor) usuario);
        } else if (usuario instanceof Aluno) {
            menuAluno((Aluno) usuario);
        }
    }

    // ==================== MENU SECRETARIA ====================

    private static void menuSecretaria(Secretaria secretaria) {
        boolean logado = true;
        while (logado) {
            System.out.println("\n----- MENU SECRETARIA (" + secretaria.getNome() + ") -----");
            System.out.println(" 1. Cadastrar curso");
            System.out.println(" 2. Cadastrar disciplina");
            System.out.println(" 3. Associar disciplina a um curso");
            System.out.println(" 4. Cadastrar professor");
            System.out.println(" 5. Cadastrar aluno");
            System.out.println(" 6. Listar cursos, disciplinas, professores e alunos");
            System.out.println(" 7. Inativar curso, disciplina ou usuario");
            System.out.println(" 8. Criar semestre");
            System.out.println(" 9. Gerar/editar curriculo semestral (adicionar ofertas)");
            System.out.println("10. Abrir periodo de matriculas");
            System.out.println("11. Encerrar periodo de matriculas (avaliar ofertas)");
            System.out.println(" 0. Logout");
            System.out.print("Escolha uma opcao: ");
            String opcao = SCANNER.nextLine().trim();

            try {
                switch (opcao) {
                    case "1":
                        cadastrarCurso();
                        break;
                    case "2":
                        cadastrarDisciplina();
                        break;
                    case "3":
                        associarDisciplinaACurso();
                        break;
                    case "4":
                        cadastrarProfessor();
                        break;
                    case "5":
                        cadastrarAluno();
                        break;
                    case "6":
                        listarDadosAcademicos();
                        break;
                    case "7":
                        inativarDados();
                        break;
                    case "8":
                        criarSemestre();
                        break;
                    case "9":
                        gerarCurriculoSemestral();
                        break;
                    case "10":
                        abrirPeriodoMatricula(secretaria);
                        break;
                    case "11":
                        encerrarPeriodoMatricula(secretaria);
                        break;
                    case "0":
                        logado = false;
                        System.out.println("Logout realizado.");
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void cadastrarCurso() {
        System.out.print("Codigo do curso: ");
        String codigo = SCANNER.nextLine().trim();
        System.out.print("Nome do curso: ");
        String nome = SCANNER.nextLine().trim();
        System.out.print("Numero de creditos: ");
        int creditos = Integer.parseInt(SCANNER.nextLine().trim());
        Curso curso = CADASTRO_SERVICE.cadastrarCurso(codigo, nome, creditos);
        System.out.println("Curso cadastrado: " + curso);
    }

    private static void cadastrarDisciplina() {
        System.out.print("Codigo da disciplina: ");
        String codigo = SCANNER.nextLine().trim();
        System.out.print("Nome da disciplina: ");
        String nome = SCANNER.nextLine().trim();
        System.out.print("Numero de creditos: ");
        int creditos = Integer.parseInt(SCANNER.nextLine().trim());
        Disciplina disciplina = CADASTRO_SERVICE.cadastrarDisciplina(codigo, nome, creditos);
        System.out.println("Disciplina cadastrada: " + disciplina);
    }

    private static void associarDisciplinaACurso() {
        Curso curso = selecionarCurso();
        if (curso == null)
            return;
        Disciplina disciplina = selecionarDisciplina();
        if (disciplina == null)
            return;
        System.out.print("Tipo (1-Obrigatoria / 2-Optativa): ");
        String tipoOpcao = SCANNER.nextLine().trim();
        TipoDisciplina tipo = tipoOpcao.equals("1") ? TipoDisciplina.OBRIGATORIA : TipoDisciplina.OPTATIVA;
        CADASTRO_SERVICE.associarDisciplinaAoCurso(curso, disciplina, tipo);
        System.out.println("Disciplina associada ao curso com sucesso.");
    }

    private static void cadastrarProfessor() {
        System.out.print("Nome do professor: ");
        String nome = SCANNER.nextLine().trim();
        System.out.print("Login: ");
        String login = SCANNER.nextLine().trim();
        System.out.print("Senha: ");
        String senha = SCANNER.nextLine().trim();
        System.out.print("Registro funcional: ");
        String registro = SCANNER.nextLine().trim();
        Professor professor = CADASTRO_SERVICE.cadastrarProfessor(
                UUID.randomUUID().toString(), nome, login, senha, registro);
        System.out.println("Professor cadastrado: " + professor.getNome());
    }

    private static void cadastrarAluno() {
        System.out.print("Nome do aluno: ");
        String nome = SCANNER.nextLine().trim();
        System.out.print("Login: ");
        String login = SCANNER.nextLine().trim();
        System.out.print("Senha: ");
        String senha = SCANNER.nextLine().trim();
        System.out.print("Numero de matricula: ");
        String matricula = SCANNER.nextLine().trim();
        Curso curso = selecionarCurso();
        if (curso == null)
            return;
        Aluno aluno = CADASTRO_SERVICE.cadastrarAluno(
                UUID.randomUUID().toString(), nome, login, senha, matricula, curso);
        System.out.println("Aluno cadastrado: " + aluno.getNome() + " (curso: " + curso.getNome() + ")");
    }

    private static void listarDadosAcademicos() {
        System.out.println("\n-- Cursos --");
        CADASTRO_SERVICE.listarCursos().forEach(System.out::println);
        System.out.println("\n-- Disciplinas --");
        CADASTRO_SERVICE.listarDisciplinas().forEach(System.out::println);
        System.out.println("\n-- Professores --");
        CADASTRO_SERVICE.listarProfessores()
                .forEach(p -> System.out.println(p.getNome() + " (" + p.getRegistro() + ")"));
        System.out.println("\n-- Alunos --");
        CADASTRO_SERVICE.listarAlunos().forEach(a -> System.out.println(
                a.getNome() + " - matricula " + a.getMatricula()
                        + (a.getCurso() != null ? " - curso " + a.getCurso().getNome() : "")));
    }

    private static void inativarDados() {
        System.out.println("1. Inativar curso  2. Inativar disciplina  3. Inativar usuario (aluno/professor)");
        System.out.print("Opcao: ");
        String opcao = SCANNER.nextLine().trim();
        switch (opcao) {
            case "1":
                Curso curso = selecionarCurso();
                if (curso != null) {
                    CADASTRO_SERVICE.inativarCurso(curso);
                    System.out.println("Curso inativado (historico academico preservado).");
                }
                break;
            case "2":
                Disciplina disciplina = selecionarDisciplina();
                if (disciplina != null) {
                    CADASTRO_SERVICE.inativarDisciplina(disciplina);
                    System.out.println("Disciplina inativada.");
                }
                break;
            case "3":
                System.out.print("Login do usuario a inativar: ");
                String login = SCANNER.nextLine().trim();
                REPOSITORIO.getUsuarios().stream()
                        .filter(u -> u.getLogin().equals(login))
                        .findFirst()
                        .ifPresentOrElse(u -> {
                            CADASTRO_SERVICE.inativarUsuario(u);
                            System.out.println("Usuario " + u.getNome() + " inativado.");
                        }, () -> System.out.println("Usuario nao encontrado."));
                break;
            default:
                System.out.println("Opcao invalida.");
        }
    }

    private static void criarSemestre() {
        System.out.print("Ano (ex: 2026): ");
        int ano = Integer.parseInt(SCANNER.nextLine().trim());
        System.out.print("Numero do semestre (1 ou 2): ");
        int numero = Integer.parseInt(SCANNER.nextLine().trim());
        Semestre semestre = new Semestre(UUID.randomUUID().toString(), ano, numero);
        semestre.iniciar();
        REPOSITORIO.getSemestres().add(semestre);
        System.out.println("Semestre criado: " + semestre);
    }

    private static void gerarCurriculoSemestral() {
        Curso curso = selecionarCurso();
        if (curso == null)
            return;
        Semestre semestre = selecionarSemestre();
        if (semestre == null)
            return;

        CurriculoSemestral curriculo = CURRICULO_SERVICE.criarCurriculo(curso, semestre);
        System.out.println("Curriculo em edicao: " + curriculo);

        boolean adicionando = true;
        while (adicionando) {
            System.out.println("\n1. Adicionar oferta de disciplina  2. Publicar curriculo  0. Voltar");
            System.out.print("Opcao: ");
            String opcao = SCANNER.nextLine().trim();
            if (opcao.equals("1")) {
                Disciplina disciplina = selecionarDisciplina();
                if (disciplina == null)
                    continue;
                Professor professor = selecionarProfessor();
                if (professor == null)
                    continue;
                OfertaDisciplina oferta = CURRICULO_SERVICE.adicionarOferta(curriculo, disciplina, professor);
                System.out.println("Oferta criada: " + oferta);
            } else if (opcao.equals("2")) {
                curriculo.publicar();
                System.out.println("Curriculo publicado para consulta dos alunos.");
                adicionando = false;
            } else if (opcao.equals("0")) {
                adicionando = false;
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void abrirPeriodoMatricula(Secretaria secretaria) {
        Semestre semestre = selecionarSemestre();
        if (semestre == null)
            return;
        System.out.print("Duracao do periodo em dias a partir de agora: ");
        long dias = Long.parseLong(SCANNER.nextLine().trim());
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusDays(dias);
        PeriodoMatricula periodo = new PeriodoMatricula(UUID.randomUUID().toString(), inicio, fim);
        semestre.setPeriodoMatricula(periodo);
        secretaria.abrirPeriodoMatricula(periodo);
        System.out.println("Periodo de matriculas aberto: " + periodo);
    }

    private static void encerrarPeriodoMatricula(Secretaria secretaria) {
        Semestre semestre = selecionarSemestre();
        if (semestre == null)
            return;
        secretaria.encerrarPeriodoMatricula(semestre.getPeriodoMatricula());
        CURRICULO_SERVICE.encerrarPeriodo(semestre);
        CURRICULO_SERVICE.avaliarOfertas(semestre);
        System.out.println("Periodo encerrado. Resultado da avaliacao das ofertas:");
        for (CurriculoSemestral curriculo : CURRICULO_SERVICE.listarCurriculosDoSemestre(semestre)) {
            for (OfertaDisciplina oferta : curriculo.getOfertas()) {
                System.out.println(" - " + oferta);
            }
        }
        List<OfertaDisciplina> canceladas = CURRICULO_SERVICE.listarOfertasCanceladas(semestre);
        if (!canceladas.isEmpty()) {
            System.out.println("\nOfertas canceladas (menos de 3 alunos) e alunos afetados:");
            for (OfertaDisciplina oferta : canceladas) {
                System.out.println(" - " + oferta.getDisciplina().getNome() + ":");
                for (Aluno afetado : CURRICULO_SERVICE.identificarAlunosAfetados(oferta)) {
                    System.out.println("     * " + afetado.getNome());
                }
            }
        }
    }

    // ==================== MENU PROFESSOR / HU10 ====================

    private static void menuProfessor(Professor professor) {
        boolean logado = true;
        while (logado) {
            System.out.println("\n----- MENU PROFESSOR (" + professor.getNome() + ") -----");
            System.out.println("1. Consultar minhas ofertas de disciplina");
            System.out.println("2. Consultar alunos matriculados em uma oferta");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opcao: ");
            String opcao = SCANNER.nextLine().trim();

            switch (opcao) {
                case "1":
                    List<OfertaDisciplina> ofertas = professor.consultarOfertas();
                    if (ofertas.isEmpty()) {
                        System.out.println("Nenhuma oferta associada a voce ainda.");
                    } else {
                        for (int i = 0; i < ofertas.size(); i++) {
                            System.out.println((i + 1) + ". " + ofertas.get(i));
                        }
                    }
                    break;
                case "2":
                    OfertaDisciplina oferta = selecionarOfertaDoProfessor(professor);
                    if (oferta == null)
                        break;
                    List<Aluno> alunos = professor.consultarAlunosMatriculados(oferta);
                    if (alunos.isEmpty()) {
                        System.out.println("Nenhum aluno matriculado ativamente nesta oferta.");
                    } else {
                        alunos.forEach(a -> System.out.println(" - " + a.getNome() + " (" + a.getMatricula() + ")"));
                    }
                    break;
                case "0":
                    logado = false;
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    // ==================== MENU ALUNO / HU05, HU06, HU08 ====================

    private static void menuAluno(Aluno aluno) {
        boolean logado = true;
        while (logado) {
            System.out.println("\n----- MENU ALUNO (" + aluno.getNome() + ") -----");
            System.out.println("1. Consultar disciplinas ofertadas");
            System.out.println("2. Realizar matricula");
            System.out.println("3. Consultar minhas matriculas");
            System.out.println("4. Cancelar matricula");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opcao: ");
            String opcao = SCANNER.nextLine().trim();

            Semestre semestre;
            switch (opcao) {
                case "1":
                    semestre = selecionarSemestre();
                    if (semestre == null)
                        break;
                    List<OfertaDisciplina> ofertas = CURRICULO_SERVICE.consultarOfertasDoAluno(aluno, semestre);
                    if (ofertas.isEmpty()) {
                        System.out.println("Nenhuma oferta disponivel para o seu curso neste semestre.");
                    } else {
                        for (int i = 0; i < ofertas.size(); i++) {
                            System.out.println((i + 1) + ". " + ofertas.get(i));
                        }
                    }
                    break;
                case "2":
                    realizarMatricula(aluno);
                    break;
                case "3":
                    semestre = selecionarSemestre();
                    if (semestre == null)
                        break;
                    List<Matricula> matriculas = aluno.consultarMatriculas(semestre);
                    if (matriculas.isEmpty()) {
                        System.out.println("Voce nao possui matriculas neste semestre.");
                    } else {
                        matriculas.forEach(m -> System.out.println(" - " + m));
                    }
                    break;
                case "4":
                    cancelarMatricula(aluno);
                    break;
                case "0":
                    logado = false;
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void realizarMatricula(Aluno aluno) {
        Semestre semestre = selecionarSemestre();
        if (semestre == null)
            return;
        List<OfertaDisciplina> ofertas = CURRICULO_SERVICE.consultarOfertasDoAluno(aluno, semestre);
        if (ofertas.isEmpty()) {
            System.out.println("Nenhuma oferta disponivel para matricula.");
            return;
        }
        for (int i = 0; i < ofertas.size(); i++) {
            System.out.println((i + 1) + ". " + ofertas.get(i));
        }
        System.out.print("Escolha o numero da oferta desejada: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= ofertas.size()) {
            System.out.println("Opcao invalida.");
            return;
        }
        Matricula matricula = MATRICULA_SERVICE.realizarMatricula(aluno, ofertas.get(indice));
        if (matricula != null) {
            System.out.println("Matricula confirmada em: " + matricula.getOferta().getDisciplina().getNome());
        } else {
            System.out.println("Nao foi possivel realizar a matricula. Verifique periodo, vagas, "
                    + "duplicidade ou limite de disciplinas (4 obrigatorias / 2 optativas).");
        }
    }

    private static void cancelarMatricula(Aluno aluno) {
        Semestre semestre = selecionarSemestre();
        if (semestre == null)
            return;
        List<Matricula> matriculas = aluno.consultarMatriculas(semestre);
        if (matriculas.isEmpty()) {
            System.out.println("Voce nao possui matriculas neste semestre.");
            return;
        }
        for (int i = 0; i < matriculas.size(); i++) {
            System.out.println((i + 1) + ". " + matriculas.get(i));
        }
        System.out.print("Escolha o numero da matricula a cancelar: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= matriculas.size()) {
            System.out.println("Opcao invalida.");
            return;
        }
        boolean cancelada = MATRICULA_SERVICE.cancelarMatricula(aluno, matriculas.get(indice));
        System.out.println(cancelada ? "Matricula cancelada com sucesso."
                : "Nao foi possivel cancelar (verifique se o periodo ainda esta aberto).");
    }

    // ==================== SELECIONADORES AUXILIARES ====================

    private static Curso selecionarCurso() {
        List<Curso> cursos = CADASTRO_SERVICE.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return null;
        }
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println((i + 1) + ". " + cursos.get(i));
        }
        System.out.print("Escolha o curso: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= cursos.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return cursos.get(indice);
    }

    private static Disciplina selecionarDisciplina() {
        List<Disciplina> disciplinas = CADASTRO_SERVICE.listarDisciplinas();
        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada.");
            return null;
        }
        for (int i = 0; i < disciplinas.size(); i++) {
            System.out.println((i + 1) + ". " + disciplinas.get(i));
        }
        System.out.print("Escolha a disciplina: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= disciplinas.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return disciplinas.get(indice);
    }

    private static Professor selecionarProfessor() {
        List<Professor> professores = CADASTRO_SERVICE.listarProfessores();
        if (professores.isEmpty()) {
            System.out.println("Nenhum professor cadastrado.");
            return null;
        }
        for (int i = 0; i < professores.size(); i++) {
            System.out.println((i + 1) + ". " + professores.get(i).getNome());
        }
        System.out.print("Escolha o professor: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= professores.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return professores.get(indice);
    }

    private static Semestre selecionarSemestre() {
        List<Semestre> semestres = REPOSITORIO.getSemestres();
        if (semestres.isEmpty()) {
            System.out.println("Nenhum semestre cadastrado.");
            return null;
        }
        for (int i = 0; i < semestres.size(); i++) {
            System.out.println((i + 1) + ". " + semestres.get(i));
        }
        System.out.print("Escolha o semestre: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= semestres.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return semestres.get(indice);
    }

    private static OfertaDisciplina selecionarOfertaDoProfessor(Professor professor) {
        List<OfertaDisciplina> ofertas = professor.consultarOfertas();
        if (ofertas.isEmpty()) {
            System.out.println("Voce nao possui ofertas associadas.");
            return null;
        }
        for (int i = 0; i < ofertas.size(); i++) {
            System.out.println((i + 1) + ". " + ofertas.get(i));
        }
        System.out.print("Escolha a oferta: ");
        int indice = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
        if (indice < 0 || indice >= ofertas.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return ofertas.get(indice);
    }
}
