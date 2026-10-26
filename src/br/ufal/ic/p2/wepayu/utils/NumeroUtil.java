package br.ufal.ic.p2.wepayu.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;

public final class NumeroUtil {
	private NumeroUtil() {
	}

	public static double converter(String texto) {
		return Double.parseDouble(texto.trim().replace(",", "."));
	}

	public static double converterNaoNegativo(String texto, String msgNulo, String msgNumerico,
			String msgNegativo) throws ValorInvalidoException {
		if (texto == null || texto.isBlank()) {
			throw new ValorInvalidoException(msgNulo);
		}
		double valor;
		try {
			valor = converter(texto);
		} catch (NumberFormatException e) {
			throw new ValorInvalidoException(msgNumerico);
		}
		if (valor < 0) {
			throw new ValorInvalidoException(msgNegativo);
		}
		return valor;
	}

	public static double converterPositivo(String texto, String msgNaoNumerico, String msgNaoPositivo)
			throws ValorInvalidoException {
		if (texto == null || texto.isBlank()) {
			throw new ValorInvalidoException(msgNaoNumerico);
		}
		double valor;
		try {
			valor = converter(texto);
		} catch (NumberFormatException e) {
			throw new ValorInvalidoException(msgNaoNumerico);
		}
		if (valor <= 0) {
			throw new ValorInvalidoException(msgNaoPositivo);
		}
		return valor;
	}

	public static BigDecimal decimal(double valor) {
		return BigDecimal.valueOf(valor);
	}

	public static BigDecimal truncar(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.DOWN);
	}

	public static String formatarDinheiro(BigDecimal valor) {
		return truncar(valor).toPlainString().replace('.', ',');
	}

	public static String formatarDinheiro(double valor) {
		return formatarDinheiro(decimal(valor));
	}

	public static String formatarHoras(BigDecimal horas) {
		if (horas.signum() == 0) {
			return "0";
		}
		return horas.stripTrailingZeros().toPlainString().replace('.', ',');
	}
}
