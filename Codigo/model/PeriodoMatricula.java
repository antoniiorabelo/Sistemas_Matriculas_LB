package Codigo.model;

import Codigo.enums.StatusPeriodo;
import java.time.LocalDateTime;

public class PeriodoMatricula {
    private String id;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusPeriodo status;

    public PeriodoMatricula(String id, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusPeriodo.PLANEJADO;
    }

    public void abrir() {
        status = StatusPeriodo.ABERTO;
    }

    public void encerrar() {
        status = StatusPeriodo.ENCERRADO;
    }

    public boolean estaAberto() {
        return status == StatusPeriodo.ABERTO && validarDataAtual();
    }

    public boolean validarDataAtual() {
        LocalDateTime agora = LocalDateTime.now();
        return !agora.isBefore(dataInicio) && !agora.isAfter(dataFim);
    }

    public String getId() {
        return id;
    }

    public StatusPeriodo getStatus() {
        return status;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    @Override
    public String toString() {
        return "Periodo[" + status + ", de " + dataInicio + " ate " + dataFim + "]";
    }
}
