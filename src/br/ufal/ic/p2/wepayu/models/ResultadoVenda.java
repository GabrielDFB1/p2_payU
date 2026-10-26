package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class ResultadoVenda {
	private String data;
	private double valor;

	public ResultadoVenda() {
	}

	public ResultadoVenda(LocalDate data, double valor) {
		this.data = data.toString();
		this.valor = valor;
	}

	public LocalDate getDataLocal() {
		return LocalDate.parse(data);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}
