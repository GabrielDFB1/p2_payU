package br.ufal.ic.p2.wepayu.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;

public class DataUtil {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DataUtil() {
    }
    public static LocalDate converter(String data, String mensagemErro) throws DataInvalidaException {
        if (data == null || data.isBlank()) {
            throw new DataInvalidaException(mensagemErro);
        }
        try {
            return LocalDate.parse(data, FORMATO);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException(mensagemErro);
        }
    }
}
