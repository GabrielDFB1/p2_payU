package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class EmpregadoAssalariado extends Empregado {

    public EmpregadoAssalariado() {
    }

    public EmpregadoAssalariado(String nome, String endereco, double salario) {
        super(nome, endereco, salario);
    }

    @Override
    public String tipo() {
        return "assalariado";
    }

    @Override
    public boolean recebeEm(LocalDate data) {
        LocalDate ultimoDiaUtil = data.withDayOfMonth(data.lengthOfMonth());
        while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
            ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
        }
        return data.equals(ultimoDiaUtil);
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate data) {
        return data.withDayOfMonth(1);
    }

    @Override
    public int diasPeriodo(LocalDate data) {
        return data.lengthOfMonth();
    }

    @Override
    public BigDecimal salarioBruto(LocalDate data) {
        return BigDecimal.valueOf(getSalario()).setScale(2, RoundingMode.DOWN);
    }
}
