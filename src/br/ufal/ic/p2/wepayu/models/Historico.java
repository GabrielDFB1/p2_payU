package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayDeque;
import java.util.Deque;

import br.ufal.ic.p2.wepayu.Exception.ComandoException;

public class Historico {
	private final Deque<String> desfazer = new ArrayDeque<>();
	private final Deque<String> refazer = new ArrayDeque<>();

	public void registrar(String estadoAnterior) {
		desfazer.push(estadoAnterior);
		refazer.clear();
	}

	public String desfazer(String estadoAtual) throws ComandoException {
		if (desfazer.isEmpty()) {
			throw new ComandoException("Nao ha comando a desfazer.");
		}
		refazer.push(estadoAtual);
		return desfazer.pop();
	}

	public String refazer(String estadoAtual) throws ComandoException {
		if (refazer.isEmpty()) {
			throw new ComandoException("Nao ha comando a refazer.");
		}
		desfazer.push(estadoAtual);
		return refazer.pop();
	}
}
