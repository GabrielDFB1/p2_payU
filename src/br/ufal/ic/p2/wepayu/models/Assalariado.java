package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

import br.ufal.ic.p2.wepayu.Exception.WePayUException;

public class Assalariado extends Empregado {
	public Assalariado() {
		super();
	}

	public Assalariado(String nome, String endereco, String salario) throws WePayUException {
		super(nome, endereco, salario);
	}

	@Override
	public String getTipo() {
		return "assalariado";
	}

	@Override
	public String getAgendaPadrao() {
		return AgendaPagamento.MENSAL;
	}

	@Override
	public Contracheque calcularPagamento(LocalDate data, AgendaPagamento agenda) {
		Contracheque contracheque = new Contracheque(this);
		contracheque.setBruto(agenda.salarioFixoDoPeriodo(getSalario()));
		return contracheque;
	}
}
