package br.ufal.ic.p2.wepayu.utils;

import java.time.LocalDate;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;

public class Intervalo {
	private final LocalDate inicio;
	private final LocalDate fim;

	public Intervalo(LocalDate inicio, LocalDate fim) {
		this.inicio = inicio;
		this.fim = fim;
	}

	public static Intervalo de(String dataInicial, String dataFinal) throws DataInvalidaException {
		LocalDate inicio = DataUtil.converter(dataInicial, "Data inicial invalida.");
		LocalDate fim = DataUtil.converter(dataFinal, "Data final invalida.");
		if (inicio.isAfter(fim)) {
			throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final.");
		}
		return new Intervalo(inicio, fim);
	}

	public static Intervalo periodoDePagamento(LocalDate pagamentoAnterior, LocalDate dataPagamento) {
		return new Intervalo(pagamentoAnterior.plusDays(1), dataPagamento.plusDays(1));
	}

	public boolean contem(LocalDate data) {
		return !data.isBefore(inicio) && data.isBefore(fim);
	}
}
