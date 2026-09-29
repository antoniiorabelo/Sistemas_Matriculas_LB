package Codigo;

import Codigo.enums.TipoDisciplina;
import Codigo.gateway.SistemaCobrancasGatewayImpl;
import Codigo.model.*;
import Codigo.repository.PersistenciaArquivo;
import Codigo.repository.Repositorio;
import Codigo.service.AutenticacaoService;
import Codigo.service.CadastroService;
import Codigo.service.CurriculoService;
import Codigo.service.MatriculaService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Sistema de Matriculas - versao console.
 *
 * Ao iniciar, carrega os dados dos arquivos .txt da pasta de dados
 * (Codigo/data). Apos cada operacao dos menus, os dados sao gravados
 * novamente, de modo que nada se perde ao fechar o programa.
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Repositorio REPOSITORIO = new Repositorio();
    private static final PersistenciaArquivo PERSISTENCIA =
            new PersistenciaArquivo(PersistenciaArquivo.resolverPastaPadrao());
    private static final AutenticacaoService AUTENTICACAO_SERVICE =
            new AutenticacaoService(REPOSITORIO.getUsuarios());
    private static final CadastroService CADASTRO_SERVICE = new CadastroService(
            REPOSITORIO.getCursos(), REPOSITORIO.getDisciplinas(),
            REPOSITORIO.getUsuarios(), AUTENTICACAO_SERVICE);
    private static final CurriculoService CURRICULO_SERVICE =
            new CurriculoService(REPOSITORIO.getCurriculos());
    private static final MatriculaService MATRICULA_SERVICE = new MatriculaService(
            new SistemaCobrancasGatewayImpl(), REPOSITORIO.getNotificacoes());

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println(" SISTEMA DE MATRICULAS - MODO TERMINAL");
        System.out.println("=======================================");

        if (!carregarDados()) {
            return;
        }
        bootstrap();

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1. Login");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");
            String opcao = lerLinha();
            if (opcao == null) {
                break;
            }
            switch (opcao) {
                case "1":
                    fazerLogin();
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
        salvarDados();
        System.out.println("Dados salvos. Encerrando o sistema. Ate mais!");
    }

    // ==================== PERSISTENCIA ====================

    private static boolean carregarDados() {
        try {
            boolean carregou = PERSISTENCIA.carregar(REPOSITORIO);
            if (carregou) {
                System.out.println("Dados carregados de: " + PERSISTENCIA.getPasta());
            } else {
                System.out.println("Nenhum dado salvo encontrado. Os arquivos serao criados em: "
                        + PERSISTENCIA.getPasta());
            }
            return true;
        } catch (IOException | RuntimeException e) {
            // Encerra sem salvar para nao sobrescrever os arquivos existentes.
            System.out.println("ERRO ao carregar os arquivos de dados: " + e.getMessage());
            System.out.println("Verifique os arquivos em " + PERSISTENCIA.getPasta()
                    + ". O sistema foi encerrado para nao sobrescrever os dados.");
            return false;
        }
    }

    private static void salvarDados() {
        try {
            PERSISTENCIA.salvar(REPOSITORIO);
        } catch (IOException e) {
            System.out.println("ERRO ao salvar os dados: " + e.getMessage());
        }
    }

    /** Cria a conta padrao da secretaria quando ainda nao existe nenhum usuario. */
    private static void bootstrap() {
        if (REPOSITORIO.getUsuarios().isEmpty()) {
            Secretaria secretaria = new Secretaria(
                    UUID.randomUUID().toString(), "Secretaria Academica", "secretaria", "admin123", "SEC001");
            AUTENTICACAO_SERVICE.cadastrarUsuario(secretaria);
            salvarDados();
            System.out.println("Conta inicial de secretaria criada -> login: secretaria | senha: admin123");
        }
    }

    // ==================== LOGIN / HU01 ====================

    private static void fazerLogin() {
        System.out.print("Login: ");
        String login = lerLinha();
        System.out.print("Senha: ");
        String senha = lerLinha();

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
            String opcao = lerLinha();
            if (opcao == null) return;

            try {
                switch (opcao) {
                    case "1": cadastrarCurso(); break;
                    case "2": cadastrarDisciplina(); break;
                    case "3": associarDisciplinaACurso(); break;
                    case "4": cadastrarProfessor(); break;
                    case "5": cadastrarAluno(); break;
                    case "6": listarDadosAcademicos(); break;
                    case "7": inativarDados(); break;
                    case "8": criarSemestre(); break;
                    case "9": gerarCurriculoSemestral(); break;
                    case "10": abrirPeriodoMatricula(secretaria); break;
                    case "11": encerrarPeriodoMatricula(secretaria); break;
                    case "0": logado = false; System.out.println("Logout realizado."); break;
                    default: System.out.println("Opcao invalida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: digite um numero valido.");
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
            salvarDados();
        }
    }

    private static void cadastrarCurso() {
        System.out.print("Codigo do curso: ");
        String codigo = lerLinha();
        System.out.print("Nome do curso: ");
        String nome = lerLinha();
        System.out.print("Numero de creditos: ");
        int creditos = lerInteiro();
        Curso curso = CADASTRO_SERVICE.cadastrarCurso(codigo, nome, creditos);
        System.out.println("Curso cadastrado: " + curso);
    }

    private static void cadastrarDisciplina() {
        System.out.print("Codigo da disciplina: ");
        String codigo = lerLinha();
        System.out.print("Nome da disciplina: ");
        String nome = lerLinha();
        System.out.print("Numero de creditos: ");
        int creditos = lerInteiro();
        Disciplina disciplina = CADASTRO_SERVICE.cadastrarDisciplina(codigo, nome, creditos);
        System.out.println("Disciplina cadastrada: " + disciplina);
    }

    private static void associarDisciplinaACurso() {
        Curso curso = selecionar(CADASTRO_SERVICE.listarCursos(), "curso");
        if (curso == null) return;
        Disciplina disciplina = selecionar(CADASTRO_SERVICE.listarDisciplinas(), "disciplina");
        if (disciplina == null) return;
        System.out.print("Tipo (1-Obrigatoria / 2-Optativa): ");
        String tipoOpcao = lerLinha();
        TipoDisciplina tipo;
        if ("1".equals(tipoOpcao)) {
            tipo = TipoDisciplina.OBRIGATORIA;
        } else if ("2".equals(tipoOpcao)) {
            tipo = TipoDisciplina.OPTATIVA;
        } else {
            System.out.println("Tipo invalido.");
            return;
        }
        CADASTRO_SERVICE.associarDisciplinaAoCurso(curso, disciplina, tipo);
        System.out.println("Disciplina associada ao curso como " + tipo + ".");
    }

    private static void cadastrarProfessor() {
        System.out.print("Nome do professor: ");
        String nome = lerLinha();
        System.out.print("Login: ");
        String login = lerLinha();
        System.out.print("Senha: ");
        String senha = lerLinha();
        System.out.print("Registro funcional: ");
        String registro = lerLinha();
        Professor professor = CADASTRO_SERVICE.cadastrarProfessor(
                UUID.randomUUID().toString(), nome, login, senha, registro);
        System.out.println("Professor cadastrado: " + professor.getNome());
    }

    private static void cadastrarAluno() {
        if (CADASTRO_SERVICE.listarCursos().isEmpty()) {
            System.out.println("Cadastre um curso antes de cadastrar alunos.");
            return;
        }
        System.out.print("Nome do aluno: ");
        String nome = lerLinha();
        System.out.print("Login: ");
        String login = lerLinha();
        System.out.print("Senha: ");
        String senha = lerLinha();
        System.out.print("Numero de matricula: ");
        String matricula = lerLinha();
        Curso curso = selecionar(CADASTRO_SERVICE.listarCursos(), "curso");
        if (curso == null) return;
        Aluno aluno = CADASTRO_SERVICE.cadastrarAluno(
                UUID.randomUUID().toString(), nome, login, senha, matricula, curso);
        System.out.println("Aluno cadastrado: " + aluno.getNome() + " (curso: " + curso.getNome() + ")");
    }

    private static void listarDadosAcademicos() {
        System.out.println("\n-- Cursos --");
        for (Curso c : CADASTRO_SERVICE.listarCursos()) {
            System.out.println(c);
            for (ComponenteCurricular cc : c.getComponentesCurriculares()) {
                System.out.println("    * " + cc.getDisciplina().getNome() + " (" + cc.getTipo() + ")");
            }
        }
        System.out.println("\n-- Disciplinas --");
        for (Disciplina d : CADASTRO_SERVICE.listarDisciplinas()) {
            System.out.println(d);
        }
        System.out.println("\n-- Professores --");
        for (Professor p : CADASTRO_SERVICE.listarProfessores()) {
            System.out.println(p.getNome() + " (" + p.getRegistro() + ")" + (p.isAtivo() ? "" : " (inativo)"));
        }
        System.out.println("\n-- Alunos --");
        for (Aluno a : CADASTRO_SERVICE.listarAlunos()) {
            System.out.println(a.getNome() + " - matricula " + a.getMatricula()
                    + (a.getCurso() != null ? " - curso " + a.getCurso().getNome() : "")
                    + (a.isAtivo() ? "" : " (inativo)"));
        }
    }

    private static void inativarDados() {
        System.out.println("1. Inativar curso  2. Inativar disciplina  3. Inativar usuario (aluno/professor)");
        System.out.print("Opcao: ");
        String opcao = lerLinha();
        if ("1".equals(opcao)) {
            Curso curso = selecionar(CADASTRO_SERVICE.listarCursos(), "curso");
            if (curso != null) {
                CADASTRO_SERVICE.inativarCurso(curso);
                System.out.println("Curso inativado (historico academico preservado).");
            }
        } else if ("2".equals(opcao)) {
            Disciplina disciplina = selecionar(CADASTRO_SERVICE.listarDisciplinas(), "disciplina");
            if (disciplina != null) {
                CADASTRO_SERVICE.inativarDisciplina(disciplina);
                System.out.println("Disciplina inativada (historico academico preservado).");
            }
        } else if ("3".equals(opcao)) {
            System.out.print("Login do usuario a inativar: ");
            String login = lerLinha();
            Usuario usuario = CADASTRO_SERVICE.buscarUsuarioPorLogin(login);
            if (usuario == null) {
                System.out.println("Usuario nao encontrado.");
            } else if (usuario instanceof Secretaria) {
                System.out.println("Nao e permitido inativar a conta da secretaria por aqui.");
            } else {
                CADASTRO_SERVICE.inativarUsuario(usuario);
                System.out.println("Usuario " + usuario.getNome() + " inativado.");
            }
        } else {
            System.out.println("Opcao invalida.");
        }
    }

    private static void criarSemestre() {
        System.out.print("Ano (ex: 2026): ");
        int ano = lerInteiro();
        System.out.print("Numero do semestre (1 ou 2): ");
        int numero = lerInteiro();
        if (numero != 1 && numero != 2) {
            System.out.println("O numero do semestre deve ser 1 ou 2.");
            return;
        }
        for (Semestre s : REPOSITORIO.getSemestres()) {
            if (s.getAno() == ano && s.getNumero() == numero) {
                System.out.println("Este semestre ja esta cadastrado.");
                return;
            }
        }
        Semestre semestre = new Semestre(UUID.randomUUID().toString(), ano, numero);
        semestre.iniciar();
        REPOSITORIO.getSemestres().add(semestre);
        System.out.println("Semestre criado: " + semestre);
    }

    private static void gerarCurriculoSemestral() {
        Curso curso = selecionar(CADASTRO_SERVICE.listarCursos(), "curso");
        if (curso == null) return;
        Semestre semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
        if (semestre == null) return;

        CurriculoSemestral curriculo = CURRICULO_SERVICE.criarCurriculo(curso, semestre);
        System.out.println("Curriculo em edicao: " + curriculo);

        boolean editando = true;
        while (editando) {
            System.out.println("\n1. Adicionar oferta de disciplina  2. Publicar curriculo  0. Voltar");
            System.out.print("Opcao: ");
            String opcao = lerLinha();
            if ("1".equals(opcao)) {
                Disciplina disciplina = selecionar(CADASTRO_SERVICE.listarDisciplinas(), "disciplina");
                if (disciplina == null) continue;
                Professor professor = selecionar(CADASTRO_SERVICE.listarProfessores(), "professor");
                if (professor == null) continue;
                try {
                    OfertaDisciplina oferta = CURRICULO_SERVICE.adicionarOferta(curriculo, disciplina, professor);
                    System.out.println("Oferta criada: " + oferta);
                    salvarDados();
                } catch (RuntimeException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            } else if ("2".equals(opcao)) {
                curriculo.publicar();
                System.out.println("Curriculo publicado para consulta dos alunos.");
                editando = false;
            } else if ("0".equals(opcao) || opcao == null) {
                editando = false;
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void abrirPeriodoMatricula(Secretaria secretaria) {
        Semestre semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
        if (semestre == null) return;
        System.out.print("Duracao do periodo em dias a partir de agora: ");
        int dias = lerInteiro();
        if (dias <= 0) {
            System.out.println("A duracao deve ser de pelo menos 1 dia.");
            return;
        }
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusDays(dias);
        PeriodoMatricula periodo = new PeriodoMatricula(UUID.randomUUID().toString(), inicio, fim);
        semestre.setPeriodoMatricula(periodo);
        secretaria.abrirPeriodoMatricula(periodo);
        System.out.println("Periodo de matriculas aberto: " + periodo);
    }

    private static void encerrarPeriodoMatricula(Secretaria secretaria) {
        Semestre semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
        if (semestre == null) return;
        if (semestre.getPeriodoMatricula() == null) {
            System.out.println("Este semestre ainda nao teve periodo de matriculas aberto.");
            return;
        }
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
            System.out.println("\nOfertas canceladas (menos de " + Validacao.MINIMO_ALUNOS + " alunos) e alunos afetados:");
            for (OfertaDisciplina oferta : canceladas) {
                System.out.println(" - " + oferta.getDisciplina().getNome() + ":");
                List<Aluno> afetados = CURRICULO_SERVICE.identificarAlunosAfetados(oferta);
                if (afetados.isEmpty()) {
                    System.out.println("     (nenhum aluno afetado)");
                }
                for (Aluno afetado : afetados) {
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
            String opcao = lerLinha();
            if (opcao == null) return;

            try {
                switch (opcao) {
                    case "1":
                        List<OfertaDisciplina> ofertas = professor.consultarOfertas();
                        if (ofertas.isEmpty()) {
                            System.out.println("Nenhuma oferta associada a voce ainda.");
                        } else {
                            for (int i = 0; i < ofertas.size(); i++) {
                                System.out.println((i + 1) + ". " + ofertas.get(i)
                                        + " - " + ofertas.get(i).getCurriculo().getSemestre());
                            }
                        }
                        break;
                    case "2":
                        OfertaDisciplina oferta = selecionar(professor.consultarOfertas(), "oferta");
                        if (oferta == null) break;
                        List<Aluno> alunos = professor.consultarAlunosMatriculados(oferta);
                        if (alunos.isEmpty()) {
                            System.out.println("Nenhum aluno com matricula ativa nesta oferta.");
                        } else {
                            for (Aluno a : alunos) {
                                System.out.println(" - " + a.getNome() + " (" + a.getMatricula() + ")");
                            }
                        }
                        break;
                    case "0":
                        logado = false;
                        System.out.println("Logout realizado.");
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: digite um numero valido.");
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
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
            String opcao = lerLinha();
            if (opcao == null) return;

            try {
                Semestre semestre;
                switch (opcao) {
                    case "1":
                        semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
                        if (semestre == null) break;
                        List<OfertaDisciplina> ofertas = CURRICULO_SERVICE.consultarOfertasDoAluno(aluno, semestre);
                        if (ofertas.isEmpty()) {
                            System.out.println("Nenhuma oferta disponivel para o seu curso neste semestre.");
                        } else {
                            for (int i = 0; i < ofertas.size(); i++) {
                                System.out.println((i + 1) + ". " + descreverOfertaParaAluno(aluno, ofertas.get(i)));
                            }
                        }
                        break;
                    case "2":
                        realizarMatricula(aluno);
                        break;
                    case "3":
                        semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
                        if (semestre == null) break;
                        List<Matricula> matriculas = aluno.consultarMatriculas(semestre);
                        if (matriculas.isEmpty()) {
                            System.out.println("Voce nao possui matriculas neste semestre.");
                        } else {
                            for (Matricula m : matriculas) {
                                System.out.println(" - " + m);
                            }
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
            } catch (NumberFormatException e) {
                System.out.println("Erro: digite um numero valido.");
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
            salvarDados();
        }
    }

    private static String descreverOfertaParaAluno(Aluno aluno, OfertaDisciplina oferta) {
        String tipo = "";
        for (ComponenteCurricular cc : aluno.getCurso().getComponentesCurriculares()) {
            if (cc.getDisciplina().equals(oferta.getDisciplina())) {
                tipo = " [" + cc.getTipo() + "]";
            }
        }
        String vagas = oferta.possuiVaga()
                ? oferta.consultarVagasDisponiveis() + " vaga(s)"
                : "SEM VAGAS";
        return oferta.getDisciplina().getNome() + tipo + " - Prof. " + oferta.getProfessor().getNome()
                + " - " + vagas + " - " + oferta.getStatus();
    }

    private static void realizarMatricula(Aluno aluno) {
        Semestre semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
        if (semestre == null) return;
        List<OfertaDisciplina> ofertas = CURRICULO_SERVICE.consultarOfertasDoAluno(aluno, semestre);
        if (ofertas.isEmpty()) {
            System.out.println("Nenhuma oferta disponivel para matricula.");
            return;
        }
        for (int i = 0; i < ofertas.size(); i++) {
            System.out.println((i + 1) + ". " + descreverOfertaParaAluno(aluno, ofertas.get(i)));
        }
        System.out.print("Escolha o numero da oferta desejada: ");
        int indice = lerInteiro() - 1;
        if (indice < 0 || indice >= ofertas.size()) {
            System.out.println("Opcao invalida.");
            return;
        }
        Matricula matricula = MATRICULA_SERVICE.realizarMatricula(aluno, ofertas.get(indice));
        if (matricula != null) {
            System.out.println("Matricula confirmada em: " + matricula.getOferta().getDisciplina().getNome());
            System.out.println("Suas disciplinas ativas neste semestre:");
            for (Matricula m : aluno.consultarMatriculas(semestre)) {
                if (m.estaAtiva()) {
                    System.out.println(" - " + m.getOferta().getDisciplina().getNome());
                }
            }
        } else {
            System.out.println("Nao foi possivel realizar a matricula: " + MATRICULA_SERVICE.getUltimoErro());
        }
    }

    private static void cancelarMatricula(Aluno aluno) {
        Semestre semestre = selecionar(REPOSITORIO.getSemestres(), "semestre");
        if (semestre == null) return;
        List<Matricula> matriculas = aluno.consultarMatriculas(semestre);
        if (matriculas.isEmpty()) {
            System.out.println("Voce nao possui matriculas neste semestre.");
            return;
        }
        Matricula matricula = selecionar(matriculas, "matricula a cancelar");
        if (matricula == null) return;
        boolean cancelada = MATRICULA_SERVICE.cancelarMatricula(aluno, matricula);
        System.out.println(cancelada
                ? "Matricula cancelada com sucesso. A vaga foi liberada."
                : "Nao foi possivel cancelar: " + MATRICULA_SERVICE.getUltimoErro());
    }

    // ==================== AUXILIARES DE ENTRADA ====================

    /** Lista os itens numerados e retorna o escolhido (ou null se invalido/vazio). */
    private static <T> T selecionar(List<T> itens, String descricao) {
        if (itens.isEmpty()) {
            System.out.println("Nenhum(a) " + descricao + " cadastrado(a).");
            return null;
        }
        for (int i = 0; i < itens.size(); i++) {
            Object item = itens.get(i);
            String texto = item instanceof Usuario ? ((Usuario) item).getNome() : String.valueOf(item);
            System.out.println((i + 1) + ". " + texto);
        }
        System.out.print("Escolha o(a) " + descricao + ": ");
        int indice = lerInteiro() - 1;
        if (indice < 0 || indice >= itens.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return itens.get(indice);
    }

    private static String lerLinha() {
        if (!SCANNER.hasNextLine()) {
            return null;
        }
        return SCANNER.nextLine().trim();
    }

    private static int lerInteiro() {
        String valor = lerLinha();
        if (valor == null) {
            throw new NumberFormatException("entrada vazia");
        }
        return Integer.parseInt(valor);
    }
}
