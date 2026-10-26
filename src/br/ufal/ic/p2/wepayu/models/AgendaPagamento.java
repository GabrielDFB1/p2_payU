package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import br.ufal.ic.p2.wepayu.Exception.AgendaPagamentoException;
import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public class AgendaPagamento {
	public static final String SEMANAL = "semanal 5";
	public static final String MENSAL = "mensal $";
	public static final String QUINZENAL = "semanal 2 5";

	private static final LocalDate DATA_REFERENCIA = LocalDate.of(2005, 1, 1);

	private final boolean mensal;

	private final int dia;

	private final int semanas;

	private AgendaPagamento(boolean mensal, int dia, int semanas) {
		this.mensal = mensal;
		this.dia = dia;
		this.semanas = semanas;
	}

	public static AgendaPagamento de(String descricao) throws AgendaPagamentoException {
		if (descricao == null) {
			throw invalida();
		}
		String[] partes = descricao.trim().split("\\s+");
		if (partes[0].equals("mensal") && partes.length == 2) {
			if (partes[1].equals("$")) {
				return new AgendaPagamento(true, 0, 0);
			}
			return new AgendaPagamento(true, inteiroEntre(partes[1], 1, 28), 0);
		}
		if (partes[0].equals("semanal") && partes.length == 2) {
			return new AgendaPagamento(false, inteiroEntre(partes[1], 1, 7), 1);
		}
		if (partes[0].equals("semanal") && partes.length == 3) {
			int semanas = inteiroEntre(partes[1], 1, 52);
			return new AgendaPagamento(false, inteiroEntre(partes[2], 1, 7), semanas);
		}
		throw invalida();
	}

	public boolean ehDiaDePagamento(LocalDate data) {
		if (mensal) {
			return dia == 0 ? data.equals(ultimoDiaUtil(data)) : data.getDayOfMonth() == dia;
		}
		if (data.getDayOfWeek().getValue() != dia) {
			return false;
		}

		LocalDate primeiraOcorrencia = DATA_REFERENCIA.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dia)));
		long semanasDesdeAPrimeira = Math.floorDiv(ChronoUnit.DAYS.between(primeiraOcorrencia, data), 7);
		return Math.floorMod(semanasDesdeAPrimeira + 1, semanas) == 0;
	}

	public LocalDate pagamentoAnterior(LocalDate data) {
		if (!mensal) {
			return data.minusWeeks(semanas);
		}
		LocalDate mesAnterior = data.minusMonths(1);
		return dia == 0 ? ultimoDiaUtil(mesAnterior) : mesAnterior.withDayOfMonth(dia);
	}

	public BigDecimal salarioFixoDoPeriodo(double salarioMensal) {
		BigDecimal salario = NumeroUtil.decimal(salarioMensal);
		if (mensal) {
			return NumeroUtil.truncar(salario);
		}
		return salario.multiply(BigDecimal.valueOf(12L * semanas))
				.divide(BigDecimal.valueOf(52), 2, RoundingMode.DOWN);
	}

	private static LocalDate ultimoDiaUtil(LocalDate diaDoMes) {
		LocalDate dia = diaDoMes.with(TemporalAdjusters.lastDayOfMonth());
		while (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
			dia = dia.minusDays(1);
		}
		return dia;
	}

	private static int inteiroEntre(String texto, int minimo, int maximo) throws AgendaPagamentoException {
		try {
			int valor = Integer.parseInt(texto);
			if (valor < minimo || valor > maximo) {
				throw invalida();
			}
			return valor;
		} catch (NumberFormatException e) {
			throw invalida();
		}
	}

	private static AgendaPagamentoException invalida() {
		return new AgendaPagamentoException("Descricao de agenda invalida");
	}
}
