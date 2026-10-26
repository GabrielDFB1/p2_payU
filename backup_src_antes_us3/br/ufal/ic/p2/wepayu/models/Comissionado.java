package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;
import java.util.Locale;

public class Comissionado extends Empregado{
	
	private double comissao;
	
	public Comissionado() {
		super();
	}
	public Comissionado(String nome, String endereco, String salario, String comissao) throws AtributoInvalidoEmpregadoException {
		super(nome, endereco, salario);
		
		if (comissao == null || comissao.isBlank()) {
			throw new AtributoInvalidoEmpregadoException("Comissao nao pode ser nula.");
		}
		
		try {
			double valor = changeDot(comissao);

			if (valor < 0) {
				throw new AtributoInvalidoEmpregadoException("Comissao deve ser nao-negativa.");
			}
			
			this.comissao = valor;
		}	catch (NumberFormatException e) {
    			throw new AtributoInvalidoEmpregadoException("Comissao deve ser numerica.");
    		}
	}
	
	@Override
	public String getAtributo(String atributo) throws AtributoInvalidoEmpregadoException{
		if (atributo.equals("comissao")) {
			return getComissaoFormatado();
		}
		else {
		return super.getAtributo(atributo);
		}
	}
	
	@Override
	public String getTipo() {
		return "comissionado";
	}
	
	public double getComissao() {
		return this.comissao;
	}
	
	public String getComissaoFormatado() {
		return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", comissao);
	}
	public void setComissao(double comissao) {
		this.comissao = comissao;
	}
}
