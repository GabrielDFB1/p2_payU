package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DadosSistema {
	private Map<String, Empregado> empregados = new LinkedHashMap<>();
	private List<String> agendas = agendasPadrao();

	public DadosSistema() {
	}

	public static List<String> agendasPadrao() {
		List<String> agendas = new ArrayList<>();
		agendas.add(AgendaPagamento.SEMANAL);
		agendas.add(AgendaPagamento.MENSAL);
		agendas.add(AgendaPagamento.QUINZENAL);
		return agendas;
	}

	public Map<String, Empregado> getEmpregados() {
		return empregados;
	}

	public void setEmpregados(Map<String, Empregado> empregados) {
		this.empregados = empregados;
	}

	public List<String> getAgendas() {
		return agendas;
	}

	public void setAgendas(List<String> agendas) {
		this.agendas = agendas;
	}
}
