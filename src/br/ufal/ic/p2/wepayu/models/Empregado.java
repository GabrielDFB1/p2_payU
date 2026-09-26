package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Empregado {
    private String nome;
    private String endereco;
    private double salario;
    private MembroSindicato membroSindicato;
    private MetodoPagamento metodoPagamento = new EmMaos();
    private double debitoSindical;

    public Empregado() {
    }

    public Empregado(String nome, String endereco, double salario) {
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
    }

    public abstract String tipo();

    public abstract boolean recebeEm(LocalDate data);

    public abstract LocalDate inicioPeriodo(LocalDate data);

    public abstract BigDecimal salarioBruto(LocalDate data);

    public int diasPeriodo(LocalDate data) {
        return (int) ChronoUnit.DAYS.between(inicioPeriodo(data), data) + 1;
    }

    public BigDecimal descontosDevidos(LocalDate data) {
        BigDecimal total = BigDecimal.valueOf(debitoSindical);
        if (membroSindicato != null) {
            BigDecimal taxaSindical = BigDecimal.valueOf(membroSindicato.getTaxaSindical())
                    .multiply(BigDecimal.valueOf(diasPeriodo(data)));
            BigDecimal taxasServico = BigDecimal.valueOf(
                    membroSindicato.totalTaxasServico(inicioPeriodo(data), data.plusDays(1)));
            total = total.add(taxaSindical).add(taxasServico);
        }
        return total;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public MembroSindicato getMembroSindicato() {
        return membroSindicato;
    }

    public void setMembroSindicato(MembroSindicato membroSindicato) {
        this.membroSindicato = membroSindicato;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public double getDebitoSindical() {
        return debitoSindical;
    }

    public void setDebitoSindical(double debitoSindical) {
        this.debitoSindical = debitoSindical;
    }
}
