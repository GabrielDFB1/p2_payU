package br.ufal.ic.p2.wepayu.models;

import java.util.Locale;

import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;

public abstract class Empregado {
    private String nome;
    private String endereco;
    private double salario;
    private boolean sindicalizado;

    public Empregado() {
    	
    }
    public Empregado(String nome, String endereco, String salario) throws AtributoInvalidoEmpregadoException {
    	
    	validateAtributes(nome, endereco, salario);
        this.nome = nome;
        this.endereco = endereco;
        this.salario = changeDot(salario);
        this.sindicalizado = false;
        
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }


    public double getSalario() {
    	return this.salario;
    }
    
    public String getSalarioFormatado() {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", salario);
    }
    
    public boolean getSindicalizado() {
    	return this.sindicalizado;
    }
    
    public String getSindicalizadoFormatado() {
    	if (this.sindicalizado) { return "true"; }
    	else return "false";
    }
    
    public String getAtributo(String atributo) throws AtributoInvalidoEmpregadoException{
    	switch (atributo) {
    	case "nome":
    		return getNome();
    	case "endereco":
    		return getEndereco();
    	case "tipo":
    		return getTipo();
    	case "salario":
    		return getSalarioFormatado();
    	case "sindicalizado":
    		return getSindicalizadoFormatado();	
    	 default:
    		 throw new AtributoInvalidoEmpregadoException("Atributo nao existe.");
    	}		
    }
    
    protected static double changeDot(String number) {
    	return Double.parseDouble(number.replace(",", "."));
    }
    
    public abstract String getTipo();
    
    private void validateAtributes(String nome, String endereco, String salario) 
    		throws AtributoInvalidoEmpregadoException {
    		if (nome == null || nome.isBlank()) {
    			throw new AtributoInvalidoEmpregadoException("Nome nao pode ser nulo."); 
    		}
    		
    		if (endereco == null || endereco.isBlank()) {
    			throw new AtributoInvalidoEmpregadoException("Endereco nao pode ser nulo."); 
    		}
    		
    		if (salario == null || salario.isBlank()) {
    			throw new AtributoInvalidoEmpregadoException("Salario nao pode ser nulo.");
    		}
    		
    		try {
    			double valor = changeDot(salario);
    			
    			if (valor < 0) {
    				throw new AtributoInvalidoEmpregadoException("Salario deve ser nao-negativo.");
    			}
    		} catch (NumberFormatException e) {
    			throw new AtributoInvalidoEmpregadoException("Salario deve ser numerico.");
    		}
    		
    		
    }
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public void setSalario(double salario) {
		this.salario = salario;
	}
	public void setSindicalizado(boolean sindicalizado) {
		this.sindicalizado = sindicalizado;
	}

}
