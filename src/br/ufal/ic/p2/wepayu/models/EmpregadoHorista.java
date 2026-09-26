package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoHorista extends Empregado {
    private List<CartaoPonto> cartoes = new ArrayList<>();

    public EmpregadoHorista() {
    }

    public EmpregadoHorista(String nome, String endereco, double salario) {
        super(nome, endereco, salario);
    }

    @Override
    public String tipo() {
        return "horista";
    }

    @Override
    public boolean recebeEm(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate data) {
        return data.minusDays(6);
    }

    @Override
    public BigDecimal salarioBruto(LocalDate data) {
        LocalDate inicio = inicioPeriodo(data);
        LocalDate fim = data.plusDays(1);
        BigDecimal valorHora = BigDecimal.valueOf(getSalario());
        BigDecimal normais = BigDecimal.valueOf(horasNormais(inicio, fim)).multiply(valorHora);
        BigDecimal extras = BigDecimal.valueOf(horasExtras(inicio, fim)).multiply(valorHora).multiply(new BigDecimal("1.5"));
        return normais.add(extras).setScale(2, RoundingMode.DOWN);
    }

    public List<CartaoPonto> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<CartaoPonto> cartoes) {
        this.cartoes = cartoes;
    }

    public double horasNormais(LocalDate inicio, LocalDate fim) {
        double total = 0;
        for (CartaoPonto cartao : cartoes) {
            if (Conversor.estaNoPeriodo(cartao.getData(), inicio, fim)) {
                total += Math.min(cartao.getHoras(), 8);
            }
        }
        return total;
    }

    public double horasExtras(LocalDate inicio, LocalDate fim) {
        double total = 0;
        for (CartaoPonto cartao : cartoes) {
            if (Conversor.estaNoPeriodo(cartao.getData(), inicio, fim)) {
                total += Math.max(cartao.getHoras() - 8, 0);
            }
        }
        return total;
    }
}
