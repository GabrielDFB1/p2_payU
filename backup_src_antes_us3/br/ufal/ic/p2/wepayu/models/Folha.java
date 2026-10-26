package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;
import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Persistencia;

import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoNulaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNomeNaoExisteException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


public class Folha {
	private Map<String, Empregado> empregados;
	
	public Folha() {
		try {
			empregados = Persistencia.carregar();
		} catch (Exception e) {
			empregados = new LinkedHashMap<>();
		}
	}
	
	
	public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws AtributoInvalidoEmpregadoException{
		
		String id = UUID.randomUUID().toString();

		if (tipo.equals("horista")) {
			Horista newEmploye = new Horista(nome, endereco, salario);
			empregados.put(id, newEmploye);
		}
		
		else if (tipo.equals("assalariado")) {
			Assalariado newEmploye = new Assalariado(nome, endereco, salario);
			empregados.put(id, newEmploye);
		}
		
		else if  (tipo.equals("comissionado")) {
			throw new AtributoInvalidoEmpregadoException("Tipo nao aplicavel.");
		}
		
		else {
			throw new AtributoInvalidoEmpregadoException("Tipo invalido.");
		}	
		return id;
	}
	
	public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws AtributoInvalidoEmpregadoException {
		String id = UUID.randomUUID().toString();
		
		if (tipo.equals("comissionado")) {
			Comissionado newEmploye = new Comissionado(nome, endereco, salario, comissao);
			empregados.put(id, newEmploye);
		}
		else if (tipo.equals("horista") || tipo.equals("assalariado")) {
			throw new AtributoInvalidoEmpregadoException("Tipo nao aplicavel.");
		}
		
		else throw new AtributoInvalidoEmpregadoException("Tipo invalido.");
		
		return id;
	}
	
	public String getAtributoEmpregado(String empregado, String atributo) throws EmpregadoNaoExisteException, 
	IdentificacaoNulaException, AtributoInvalidoEmpregadoException {
		
		Empregado employe = buscarEmpregado(empregado);
		
		return employe.getAtributo(atributo);
	}
	
	
	private Empregado buscarEmpregado(String empregado) throws IdentificacaoNulaException, EmpregadoNaoExisteException {
		if (empregado == null || empregado.isBlank()) {
			throw new IdentificacaoNulaException("Identificacao do empregado nao pode ser nula.");
		}
		
		Empregado employe = empregados.get(empregado);
		
		if (employe == null) {
			throw new EmpregadoNaoExisteException();
		}
		
		return employe;
	}
	public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNomeNaoExisteException {
		int contador = 1;
		for (Map.Entry<String, Empregado> empregado : empregados.entrySet()) {
			if (empregado.getValue().getNome().contains(nome)) {
				
				if (contador == indice) { return empregado.getKey(); }
				contador++;
			}
		}
		throw new EmpregadoNomeNaoExisteException("Nao ha empregado com esse nome.");
	}
	
	public void removerEmpregado(String id) throws IdentificacaoNulaException, EmpregadoNaoExisteException {
		Empregado employe = buscarEmpregado(id);
		
		empregados.remove(id);
	}

	public void salvar() throws IOException {
		Persistencia.salvar(empregados);
	}

	public void zerar() {
		empregados.clear();
	}
	
}
