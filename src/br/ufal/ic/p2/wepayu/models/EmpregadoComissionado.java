package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoComissionado extends Empregado {
    private static final LocalDate DATA_CONTRATO = LocalDate.of(2005, 1, 1);

    private double comissao;
    private List<ResultadoVenda> vendas = new ArrayList<>();

    public EmpregadoComissionado() {
    }

    public EmpregadoComissionado(String nome, String endereco, double salario, double comissao) {
        super(nome, endereco, salario);
        this.comissao = comissao;
    }

    @Override
    public String tipo() {
        return "comissionado";
    }

    @Override
    public boolean recebeEm(LocalDate data) {
        long dias = ChronoUnit.DAYS.between(DATA_CONTRATO, data);
        return data.getDayOfWeek() == DayOfWeek.FRIDAY && dias >= 13 && (dias - 13) % 14 == 0;
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate data) {
        return data.minusDays(13);
    }

    @Override
    public BigDecimal salarioBruto(LocalDate data) {
        return salarioFixo().add(comissaoPeriodo(data));
    }

    public BigDecimal salarioFixo() {
        return BigDecimal.valueOf(getSalario()).multiply(BigDecimal.valueOf(24))
                .divide(BigDecimal.valueOf(52), 2, RoundingMode.DOWN);
    }

    public BigDecimal vendasPeriodo(LocalDate data) {
        return BigDecimal.valueOf(totalVendas(inicioPeriodo(data), data.plusDays(1)));
    }

    public BigDecimal comissaoPeriodo(LocalDate data) {
        return vendasPeriodo(data).multiply(BigDecimal.valueOf(comissao)).setScale(2, RoundingMode.DOWN);
    }

    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    public List<ResultadoVenda> getVendas() {
        return vendas;
    }

    public void setVendas(List<ResultadoVenda> vendas) {
        this.vendas = vendas;
    }

    public double totalVendas(LocalDate inicio, LocalDate fim) {
        BigDecimal total = BigDecimal.ZERO;
        for (ResultadoVenda venda : vendas) {
            if (Conversor.estaNoPeriodo(venda.getData(), inicio, fim)) {
                total = total.add(BigDecimal.valueOf(venda.getValor()));
            }
        }
        return total.doubleValue();
    }
}
