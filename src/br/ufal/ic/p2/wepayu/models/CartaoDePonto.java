package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class CartaoDePonto {
	private String data;
	private double horas;

	public CartaoDePonto() {
	}

	public CartaoDePonto(LocalDate data, double horas) {
		this.data = data.toString();
		this.horas = horas;
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

	public double getHoras() {
		return horas;
	}

	public void setHoras(double horas) {
		this.horas = horas;
	}
}
