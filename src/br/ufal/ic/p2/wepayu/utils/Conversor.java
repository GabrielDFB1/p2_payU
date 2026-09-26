package br.ufal.ic.p2.wepayu.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class Conversor {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static double paraDouble(String valor) {
        return Double.parseDouble(valor.replace(",", "."));
    }

    public static LocalDate paraData(String data) {
        return LocalDate.parse(data, FORMATO_DATA);
    }

    public static boolean estaNoPeriodo(String data, LocalDate inicio, LocalDate fim) {
        LocalDate dia = LocalDate.parse(data);
        return !dia.isBefore(inicio) && dia.isBefore(fim);
    }

    public static String formatarValor(double valor) {
        return formatarValor(BigDecimal.valueOf(valor));
    }

    public static String formatarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.DOWN).toPlainString().replace(".", ",");
    }

    public static String formatarHoras(double horas) {
        return BigDecimal.valueOf(horas).stripTrailingZeros().toPlainString().replace(".", ",");
    }
}
