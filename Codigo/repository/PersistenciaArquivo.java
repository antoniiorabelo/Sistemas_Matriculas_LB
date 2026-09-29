package Codigo.repository;

import Codigo.enums.*;
import Codigo.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistencia dos dados em arquivos texto (.txt), um arquivo por entidade.
 *
 * Formato: uma linha por registro, campos separados por ";".
 * Linhas iniciadas por "#" sao cabecalhos/comentarios e sao ignoradas.
 * Os relacionamentos entre objetos sao gravados pelos identificadores
 * (codigo do curso, codigo da disciplina, id do usuario, id da oferta etc.)
 * e reconstruidos na leitura.
 *
 * Arquivos gerados na pasta de dados:
 *   curso.txt, disciplina.txt, componente_curricular.txt, usuario.txt,
 *   semestre.txt, periodo_matricula.txt, curriculo_semestral.txt,
 *   oferta_disciplina.txt, matricula.txt, notificacao_cobranca.txt
 */
public class PersistenciaArquivo {

    private static final String SEP = ";";

    private static final String ARQ_CURSO = "curso.txt";
    private static final String ARQ_DISCIPLINA = "disciplina.txt";
    private static final String ARQ_COMPONENTE = "componente_curricular.txt";
    private static final String ARQ_USUARIO = "usuario.txt";
    private static final String ARQ_SEMESTRE = "semestre.txt";
    private static final String ARQ_PERIODO = "periodo_matricula.txt";
    private static final String ARQ_CURRICULO = "curriculo_semestral.txt";
    private static final String ARQ_OFERTA = "oferta_disciplina.txt";
    private static final String ARQ_MATRICULA = "matricula.txt";
    private static final String ARQ_NOTIFICACAO = "notificacao_cobranca.txt";

    private final Path pasta;

    public PersistenciaArquivo(Path pasta) {
        this.pasta = pasta;
    }

    /**
     * Usa "Codigo/data" quando o programa e executado a partir da raiz do
     * repositorio (pasta que contem "Codigo"); caso contrario, usa "data".
     */
    public static Path resolverPastaPadrao() {
        Path codigo = Paths.get("Codigo");
        if (Files.isDirectory(codigo)) {
            return codigo.resolve("data");
        }
        return Paths.get("data");
    }

    public Path getPasta() {
        return pasta.toAbsolutePath().normalize();
    }

    // =====================================================================
    // SALVAR
    // =====================================================================

    public void salvar(Repositorio repo) throws IOException {
        Files.createDirectories(pasta);

        // curso.txt
        List<String> linhas = new ArrayList<>();
        for (Curso c : repo.getCursos()) {
            linhas.add(juntar(c.getCodigo(), c.getNome(), c.getNumeroCreditos(), c.isAtivo()));
        }
        escrever(ARQ_CURSO, "codigo;nome;numeroCreditos;ativo", linhas);

        // disciplina.txt
        linhas = new ArrayList<>();
        for (Disciplina d : repo.getDisciplinas()) {
            linhas.add(juntar(d.getCodigo(), d.getNome(), d.getNumeroCreditos(), d.isAtiva()));
        }
        escrever(ARQ_DISCIPLINA, "codigo;nome;numeroCreditos;ativa", linhas);

        // componente_curricular.txt
        linhas = new ArrayList<>();
        for (Curso c : repo.getCursos()) {
            for (ComponenteCurricular cc : c.getComponentesCurriculares()) {
                linhas.add(juntar(cc.getId(), c.getCodigo(), cc.getDisciplina().getCodigo(), cc.getTipo()));
            }
        }
        escrever(ARQ_COMPONENTE, "id;codigoCurso;codigoDisciplina;tipo", linhas);

        // usuario.txt
        linhas = new ArrayList<>();
        for (Usuario u : repo.getUsuarios()) {
            String tipo;
            String registro;
            String codigoCurso = "";
            if (u instanceof Aluno) {
                Aluno a = (Aluno) u;
                tipo = "ALUNO";
                registro = a.getMatricula();
                codigoCurso = a.getCurso() == null ? "" : a.getCurso().getCodigo();
            } else if (u instanceof Professor) {
                tipo = "PROFESSOR";
                registro = ((Professor) u).getRegistro();
            } else {
                tipo = "SECRETARIA";
                registro = ((Secretaria) u).getRegistro();
            }
            linhas.add(juntar(tipo, u.getId(), u.getNome(), u.getLogin(), u.getSenha(),
                    u.isAtivo(), registro, codigoCurso));
        }
        escrever(ARQ_USUARIO, "tipo;id;nome;login;senha;ativo;registroOuMatricula;codigoCurso", linhas);

        // semestre.txt e periodo_matricula.txt
        linhas = new ArrayList<>();
        List<String> linhasPeriodo = new ArrayList<>();
        for (Semestre s : repo.getSemestres()) {
            linhas.add(juntar(s.getId(), s.getAno(), s.getNumero(), s.getStatus()));
            PeriodoMatricula p = s.getPeriodoMatricula();
            if (p != null) {
                linhasPeriodo.add(juntar(p.getId(), s.getId(), p.getDataInicio(), p.getDataFim(), p.getStatus()));
            }
        }
        escrever(ARQ_SEMESTRE, "id;ano;numero;status", linhas);
        escrever(ARQ_PERIODO, "id;idSemestre;dataInicio;dataFim;status", linhasPeriodo);

        // curriculo_semestral.txt, oferta_disciplina.txt e matricula.txt
        linhas = new ArrayList<>();
        List<String> linhasOferta = new ArrayList<>();
        List<String> linhasMatricula = new ArrayList<>();
        for (CurriculoSemestral cs : repo.getCurriculos()) {
            linhas.add(juntar(cs.getId(), cs.getCurso().getCodigo(), cs.getSemestre().getId(),
                    cs.getDataCriacao(), cs.isPublicado()));
            for (OfertaDisciplina o : cs.getOfertas()) {
                linhasOferta.add(juntar(o.getId(), cs.getId(), o.getDisciplina().getCodigo(),
                        o.getProfessor().getId(), o.getStatus()));
                for (Matricula m : o.getMatriculas()) {
                    linhasMatricula.add(juntar(m.getId(), m.getAluno().getId(), o.getId(),
                            m.getStatus(), m.getDataMatricula(), m.getDataCancelamento()));
                }
            }
        }
        escrever(ARQ_CURRICULO, "id;codigoCurso;idSemestre;dataCriacao;publicado", linhas);
        escrever(ARQ_OFERTA, "id;idCurriculo;codigoDisciplina;idProfessor;status", linhasOferta);
        escrever(ARQ_MATRICULA, "id;idAluno;idOferta;status;dataMatricula;dataCancelamento", linhasMatricula);

        // notificacao_cobranca.txt
        linhas = new ArrayList<>();
        for (NotificacaoCobranca n : repo.getNotificacoes()) {
            linhas.add(juntar(n.getId(), n.getTipoOperacao(), n.getAluno().getId(),
                    n.getSemestre().getId(), n.getStatus(), n.getDataEnvio(), n.getMensagemErro()));
        }
        escrever(ARQ_NOTIFICACAO, "id;tipoOperacao;idAluno;idSemestre;status;dataEnvio;mensagemErro", linhas);
    }

    // =====================================================================
    // CARREGAR
    // =====================================================================

    /**
     * Carrega os arquivos da pasta de dados para o repositorio.
     * Retorna false se ainda nao existir nenhum dado salvo.
     */
    public boolean carregar(Repositorio repo) throws IOException {
        if (!Files.exists(pasta.resolve(ARQ_USUARIO))) {
            return false;
        }

        // Cursos
        Map<String, Curso> cursos = new HashMap<>();
        for (String[] c : ler(ARQ_CURSO)) {
            Curso curso = new Curso(c[0], c[1], Integer.parseInt(c[2]));
            if (!Boolean.parseBoolean(c[3])) curso.inativar();
            cursos.put(curso.getCodigo(), curso);
            repo.getCursos().add(curso);
        }

        // Disciplinas
        Map<String, Disciplina> disciplinas = new HashMap<>();
        for (String[] c : ler(ARQ_DISCIPLINA)) {
            Disciplina d = new Disciplina(c[0], c[1], Integer.parseInt(c[2]));
            if (!Boolean.parseBoolean(c[3])) d.inativar();
            disciplinas.put(d.getCodigo(), d);
            repo.getDisciplinas().add(d);
        }

        // Componentes curriculares (curso x disciplina x tipo)
        for (String[] c : ler(ARQ_COMPONENTE)) {
            Curso curso = obrigatorio(cursos, c[1], ARQ_COMPONENTE);
            Disciplina disciplina = obrigatorio(disciplinas, c[2], ARQ_COMPONENTE);
            curso.adicionarComponente(new ComponenteCurricular(c[0], disciplina, TipoDisciplina.valueOf(c[3])));
        }

        // Usuarios
        Map<String, Usuario> usuarios = new HashMap<>();
        for (String[] c : ler(ARQ_USUARIO)) {
            String tipo = c[0];
            Usuario u;
            if ("ALUNO".equals(tipo)) {
                Curso curso = vazio(c[7]) ? null : obrigatorio(cursos, c[7], ARQ_USUARIO);
                u = new Aluno(c[1], c[2], c[3], c[4], c[6], curso);
            } else if ("PROFESSOR".equals(tipo)) {
                u = new Professor(c[1], c[2], c[3], c[4], c[6]);
            } else {
                u = new Secretaria(c[1], c[2], c[3], c[4], c[6]);
            }
            if (!Boolean.parseBoolean(c[5])) u.inativar();
            usuarios.put(u.getId(), u);
            repo.getUsuarios().add(u);
        }

        // Semestres
        Map<String, Semestre> semestres = new HashMap<>();
        for (String[] c : ler(ARQ_SEMESTRE)) {
            Semestre s = new Semestre(c[0], Integer.parseInt(c[1]), Integer.parseInt(c[2]));
            s.restaurarStatus(StatusSemestre.valueOf(c[3]));
            semestres.put(s.getId(), s);
            repo.getSemestres().add(s);
        }

        // Periodos de matricula
        for (String[] c : ler(ARQ_PERIODO)) {
            Semestre s = obrigatorio(semestres, c[1], ARQ_PERIODO);
            PeriodoMatricula p = new PeriodoMatricula(c[0], LocalDateTime.parse(c[2]), LocalDateTime.parse(c[3]));
            p.restaurarStatus(StatusPeriodo.valueOf(c[4]));
            s.setPeriodoMatricula(p);
        }

        // Curriculos semestrais
        Map<String, CurriculoSemestral> curriculos = new HashMap<>();
        for (String[] c : ler(ARQ_CURRICULO)) {
            CurriculoSemestral cs = new CurriculoSemestral(c[0],
                    obrigatorio(cursos, c[1], ARQ_CURRICULO),
                    obrigatorio(semestres, c[2], ARQ_CURRICULO),
                    LocalDate.parse(c[3]));
            if (Boolean.parseBoolean(c[4])) cs.publicar();
            curriculos.put(cs.getId(), cs);
            repo.getCurriculos().add(cs);
        }

        // Ofertas de disciplina
        Map<String, OfertaDisciplina> ofertas = new HashMap<>();
        for (String[] c : ler(ARQ_OFERTA)) {
            CurriculoSemestral cs = obrigatorio(curriculos, c[1], ARQ_OFERTA);
            Usuario prof = obrigatorio(usuarios, c[3], ARQ_OFERTA);
            if (!(prof instanceof Professor)) {
                throw new IOException(ARQ_OFERTA + ": o id " + c[3] + " nao pertence a um professor.");
            }
            OfertaDisciplina o = new OfertaDisciplina(c[0],
                    obrigatorio(disciplinas, c[2], ARQ_OFERTA), (Professor) prof, cs);
            o.restaurarStatus(StatusOferta.valueOf(c[4]));
            cs.adicionarOferta(o);
            ((Professor) prof).adicionarOferta(o);
            ofertas.put(o.getId(), o);
        }

        // Matriculas
        for (String[] c : ler(ARQ_MATRICULA)) {
            Usuario u = obrigatorio(usuarios, c[1], ARQ_MATRICULA);
            if (!(u instanceof Aluno)) {
                throw new IOException(ARQ_MATRICULA + ": o id " + c[1] + " nao pertence a um aluno.");
            }
            Aluno aluno = (Aluno) u;
            OfertaDisciplina oferta = obrigatorio(ofertas, c[2], ARQ_MATRICULA);
            Matricula m = new Matricula(c[0], aluno, oferta, StatusMatricula.valueOf(c[3]),
                    dataHora(c[4]), dataHora(c[5]));
            aluno.adicionarMatricula(m);
            oferta.restaurarMatricula(m);
        }

        // Notificacoes de cobranca
        for (String[] c : ler(ARQ_NOTIFICACAO)) {
            Usuario u = obrigatorio(usuarios, c[2], ARQ_NOTIFICACAO);
            NotificacaoCobranca n = new NotificacaoCobranca(c[0], TipoOperacaoCobranca.valueOf(c[1]),
                    (Aluno) u, obrigatorio(semestres, c[3], ARQ_NOTIFICACAO),
                    StatusNotificacao.valueOf(c[4]), dataHora(c[5]), vazio(c[6]) ? null : c[6]);
            repo.getNotificacoes().add(n);
        }

        return true;
    }

    // =====================================================================
    // AUXILIARES
    // =====================================================================

    /**
     * Grava primeiro em um arquivo temporario e depois substitui o original,
     * evitando arquivos corrompidos caso o programa seja interrompido no meio.
     */
    private void escrever(String nomeArquivo, String cabecalho, List<String> linhas) throws IOException {
        Path destino = pasta.resolve(nomeArquivo);
        Path temporario = pasta.resolve(nomeArquivo + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temporario, StandardCharsets.UTF_8)) {
            writer.write("# " + cabecalho);
            writer.newLine();
            for (String linha : linhas) {
                writer.write(linha);
                writer.newLine();
            }
        }
        Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    private List<String[]> ler(String nomeArquivo) throws IOException {
        List<String[]> registros = new ArrayList<>();
        Path arquivo = pasta.resolve(nomeArquivo);
        if (!Files.exists(arquivo)) {
            return registros;
        }
        try (BufferedReader reader = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                registros.add(linha.split(SEP, -1));
            }
        }
        return registros;
    }

    /** Junta os campos com ";" e remove caracteres que quebrariam o formato. */
    private static String juntar(Object... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) sb.append(SEP);
            Object valor = campos[i];
            if (valor != null) {
                sb.append(valor.toString()
                        .replace(SEP, ",")
                        .replace("\r", " ")
                        .replace("\n", " "));
            }
        }
        return sb.toString();
    }

    private static <T> T obrigatorio(Map<String, T> mapa, String chave, String arquivo) throws IOException {
        T valor = mapa.get(chave);
        if (valor == null) {
            throw new IOException(arquivo + ": referencia nao encontrada -> " + chave);
        }
        return valor;
    }

    private static LocalDateTime dataHora(String valor) {
        return vazio(valor) ? null : LocalDateTime.parse(valor);
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
