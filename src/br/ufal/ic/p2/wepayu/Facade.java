package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import br.ufal.ic.p2.wepayu.utils.Persistencia;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Facade {
    private Dados dados = Persistencia.carregar();

    public void zerarSistema() {
        dados = new Dados();
    }

    public void encerrarSistema() {
        Persistencia.salvar(dados);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws WePayUException {
        return criarEmpregado(nome, endereco, tipo, salario, null);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws WePayUException {
        if (nome == null || nome.isEmpty()) {
            throw new WePayUException("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new WePayUException("Endereco nao pode ser nulo.");
        }
        if (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado")) {
            throw new WePayUException("Tipo invalido.");
        }
        if (tipo.equals("comissionado") != (comissao != null)) {
            throw new WePayUException("Tipo nao aplicavel.");
        }

        double valorSalario = lerValor(salario, "Salario nao pode ser nulo.",
                "Salario deve ser numerico.", "Salario deve ser nao-negativo.");

        Empregado empregado;
        if (tipo.equals("horista")) {
            empregado = new EmpregadoHorista(nome, endereco, valorSalario);
        } else if (tipo.equals("assalariado")) {
            empregado = new EmpregadoAssalariado(nome, endereco, valorSalario);
        } else {
            double valorComissao = lerValor(comissao, "Comissao nao pode ser nula.",
                    "Comissao deve ser numerica.", "Comissao deve ser nao-negativa.");
            empregado = new EmpregadoComissionado(nome, endereco, valorSalario, valorComissao);
        }

        String id = String.valueOf(dados.getProximoId());
        dados.setProximoId(dados.getProximoId() + 1);
        dados.getEmpregados().put(id, empregado);
        return id;
    }

    public void removerEmpregado(String emp) throws WePayUException {
        buscarEmpregado(emp);
        dados.getEmpregados().remove(emp);
    }

    public String getAtributoEmpregado(String emp, String atributo) throws WePayUException {
        Empregado empregado = buscarEmpregado(emp);

        switch (atributo) {
            case "nome":
                return empregado.getNome();
            case "endereco":
                return empregado.getEndereco();
            case "tipo":
                return empregado.tipo();
            case "salario":
                return Conversor.formatarValor(empregado.getSalario());
            case "comissao":
                return Conversor.formatarValor(buscarComissionado(emp).getComissao());
            case "metodoPagamento":
                return empregado.getMetodoPagamento().nome();
            case "banco":
                return buscarBanco(empregado).getBanco();
            case "agencia":
                return buscarBanco(empregado).getAgencia();
            case "contaCorrente":
                return buscarBanco(empregado).getContaCorrente();
            case "sindicalizado":
                return String.valueOf(empregado.getMembroSindicato() != null);
            case "idSindicato":
                return buscarMembro(empregado).getIdMembro();
            case "taxaSindical":
                return Conversor.formatarValor(buscarMembro(empregado).getTaxaSindical());
            default:
                throw new WePayUException("Atributo nao existe.");
        }
    }

    public String getEmpregadoPorNome(String nome, int indice) throws WePayUException {
        int contador = 0;
        for (String id : dados.getEmpregados().keySet()) {
            if (dados.getEmpregados().get(id).getNome().contains(nome)) {
                contador++;
                if (contador == indice) {
                    return id;
                }
            }
        }
        throw new WePayUException("Nao ha empregado com esse nome.");
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws WePayUException {
        Empregado empregado = buscarEmpregado(emp);

        switch (atributo) {
            case "nome":
                if (valor == null || valor.isEmpty()) {
                    throw new WePayUException("Nome nao pode ser nulo.");
                }
                empregado.setNome(valor);
                break;
            case "endereco":
                if (valor == null || valor.isEmpty()) {
                    throw new WePayUException("Endereco nao pode ser nulo.");
                }
                empregado.setEndereco(valor);
                break;
            case "tipo":
                mudarTipo(emp, empregado, valor, empregado.getSalario(), 0);
                break;
            case "salario":
                empregado.setSalario(lerValor(valor, "Salario nao pode ser nulo.",
                        "Salario deve ser numerico.", "Salario deve ser nao-negativo."));
                break;
            case "comissao":
                EmpregadoComissionado comissionado = buscarComissionado(emp);
                comissionado.setComissao(lerValor(valor, "Comissao nao pode ser nula.",
                        "Comissao deve ser numerica.", "Comissao deve ser nao-negativa."));
                break;
            case "metodoPagamento":
                if (valor.equals("emMaos")) {
                    empregado.setMetodoPagamento(new EmMaos());
                } else if (valor.equals("correios")) {
                    empregado.setMetodoPagamento(new Correios());
                } else {
                    throw new WePayUException("Metodo de pagamento invalido.");
                }
                break;
            case "sindicalizado":
                if (valor.equals("false")) {
                    empregado.setMembroSindicato(null);
                } else {
                    throw new WePayUException("Valor deve ser true ou false.");
                }
                break;
            default:
                throw new WePayUException("Atributo nao existe.");
        }
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salarioOuComissao) throws WePayUException {
        Empregado empregado = buscarEmpregado(emp);
        if (!atributo.equals("tipo")) {
            throw new WePayUException("Atributo nao existe.");
        }
        if (valor.equals("comissionado")) {
            double comissao = lerValor(salarioOuComissao, "Comissao nao pode ser nula.",
                    "Comissao deve ser numerica.", "Comissao deve ser nao-negativa.");
            mudarTipo(emp, empregado, valor, empregado.getSalario(), comissao);
        } else {
            double salario = lerValor(salarioOuComissao, "Salario nao pode ser nulo.",
                    "Salario deve ser numerico.", "Salario deve ser nao-negativo.");
            mudarTipo(emp, empregado, valor, salario, 0);
        }
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente) throws WePayUException {
        Empregado empregado = buscarEmpregado(emp);
        if (!atributo.equals("metodoPagamento") || !valor.equals("banco")) {
            throw new WePayUException("Metodo de pagamento invalido.");
        }
        if (banco == null || banco.isEmpty()) {
            throw new WePayUException("Banco nao pode ser nulo.");
        }
        if (agencia == null || agencia.isEmpty()) {
            throw new WePayUException("Agencia nao pode ser nulo.");
        }
        if (contaCorrente == null || contaCorrente.isEmpty()) {
            throw new WePayUException("Conta corrente nao pode ser nulo.");
        }
        empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws WePayUException {
        Empregado empregado = buscarEmpregado(emp);
        if (!atributo.equals("sindicalizado")) {
            throw new WePayUException("Atributo nao existe.");
        }
        if (!valor.equals("true")) {
            throw new WePayUException("Valor deve ser true ou false.");
        }
        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new WePayUException("Identificacao do sindicato nao pode ser nula.");
        }
        double taxa = lerValor(taxaSindical, "Taxa sindical nao pode ser nula.",
                "Taxa sindical deve ser numerica.", "Taxa sindical deve ser nao-negativa.");

        for (Empregado outro : dados.getEmpregados().values()) {
            if (outro != empregado && outro.getMembroSindicato() != null
                    && outro.getMembroSindicato().getIdMembro().equals(idSindicato)) {
                throw new WePayUException("Ha outro empregado com esta identificacao de sindicato");
            }
        }
        empregado.setMembroSindicato(new MembroSindicato(idSindicato, taxa));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        if (membro == null || membro.isEmpty()) {
            throw new WePayUException("Identificacao do membro nao pode ser nula.");
        }
        MembroSindicato membroSindicato = null;
        for (Empregado empregado : dados.getEmpregados().values()) {
            if (empregado.getMembroSindicato() != null && empregado.getMembroSindicato().getIdMembro().equals(membro)) {
                membroSindicato = empregado.getMembroSindicato();
            }
        }
        if (membroSindicato == null) {
            throw new WePayUException("Membro nao existe.");
        }
        LocalDate dataTaxa = lerData(data, "Data invalida.");
        double valorTaxa = Conversor.paraDouble(valor);
        if (valorTaxa <= 0) {
            throw new WePayUException("Valor deve ser positivo.");
        }
        membroSindicato.getTaxasServico().add(new TaxaServico(dataTaxa.toString(), valorTaxa));
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
        MembroSindicato membro = buscarMembro(buscarEmpregado(emp));
        LocalDate[] periodo = lerPeriodo(dataInicial, dataFinal);
        return Conversor.formatarValor(membro.totalTaxasServico(periodo[0], periodo[1]));
    }

    public void lancaCartao(String emp, String data, String horas) throws WePayUException {
        EmpregadoHorista horista = buscarHorista(emp);
        LocalDate dataCartao = lerData(data, "Data invalida.");
        double valorHoras = Conversor.paraDouble(horas);
        if (valorHoras <= 0) {
            throw new WePayUException("Horas devem ser positivas.");
        }
        horista.getCartoes().add(new CartaoPonto(dataCartao.toString(), valorHoras));
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        EmpregadoHorista horista = buscarHorista(emp);
        LocalDate[] periodo = lerPeriodo(dataInicial, dataFinal);
        return Conversor.formatarHoras(horista.horasNormais(periodo[0], periodo[1]));
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        EmpregadoHorista horista = buscarHorista(emp);
        LocalDate[] periodo = lerPeriodo(dataInicial, dataFinal);
        return Conversor.formatarHoras(horista.horasExtras(periodo[0], periodo[1]));
    }

    public void lancaVenda(String emp, String data, String valor) throws WePayUException {
        EmpregadoComissionado comissionado = buscarComissionado(emp);
        LocalDate dataVenda = lerData(data, "Data invalida.");
        double valorVenda = Conversor.paraDouble(valor);
        if (valorVenda <= 0) {
            throw new WePayUException("Valor deve ser positivo.");
        }
        comissionado.getVendas().add(new ResultadoVenda(dataVenda.toString(), valorVenda));
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        EmpregadoComissionado comissionado = buscarComissionado(emp);
        LocalDate[] periodo = lerPeriodo(dataInicial, dataFinal);
        return Conversor.formatarValor(comissionado.totalVendas(periodo[0], periodo[1]));
    }

    public String totalFolha(String data) throws WePayUException {
        LocalDate dia = lerData(data, "Data invalida.");
        return Conversor.formatarValor(new FolhaDePagamento(dados.getEmpregados().values(), dia).total());
    }

    public void rodaFolha(String data, String saida) throws WePayUException {
        LocalDate dia = lerData(data, "Data invalida.");
        try {
            new FolhaDePagamento(dados.getEmpregados().values(), dia).gerar(saida);
        } catch (FileNotFoundException e) {
            throw new WePayUException("Nao foi possivel gerar o arquivo da folha.");
        }
    }

    private void mudarTipo(String id, Empregado antigo, String tipo, double salario, double comissao) throws WePayUException {
        Empregado novo;
        if (tipo.equals("horista")) {
            novo = new EmpregadoHorista(antigo.getNome(), antigo.getEndereco(), salario);
        } else if (tipo.equals("assalariado")) {
            novo = new EmpregadoAssalariado(antigo.getNome(), antigo.getEndereco(), salario);
        } else if (tipo.equals("comissionado")) {
            novo = new EmpregadoComissionado(antigo.getNome(), antigo.getEndereco(), salario, comissao);
        } else {
            throw new WePayUException("Tipo invalido.");
        }
        novo.setMembroSindicato(antigo.getMembroSindicato());
        novo.setMetodoPagamento(antigo.getMetodoPagamento());
        dados.getEmpregados().put(id, novo);
    }

    private Banco buscarBanco(Empregado empregado) throws WePayUException {
        if (!(empregado.getMetodoPagamento() instanceof Banco)) {
            throw new WePayUException("Empregado nao recebe em banco.");
        }
        return (Banco) empregado.getMetodoPagamento();
    }

    private MembroSindicato buscarMembro(Empregado empregado) throws WePayUException {
        if (empregado.getMembroSindicato() == null) {
            throw new WePayUException("Empregado nao eh sindicalizado.");
        }
        return empregado.getMembroSindicato();
    }

    private EmpregadoComissionado buscarComissionado(String id) throws WePayUException {
        Empregado empregado = buscarEmpregado(id);
        if (!(empregado instanceof EmpregadoComissionado)) {
            throw new WePayUException("Empregado nao eh comissionado.");
        }
        return (EmpregadoComissionado) empregado;
    }

    private EmpregadoHorista buscarHorista(String id) throws WePayUException {
        Empregado empregado = buscarEmpregado(id);
        if (!(empregado instanceof EmpregadoHorista)) {
            throw new WePayUException("Empregado nao eh horista.");
        }
        return (EmpregadoHorista) empregado;
    }

    private LocalDate lerData(String data, String erro) throws WePayUException {
        try {
            return Conversor.paraData(data);
        } catch (DateTimeParseException e) {
            throw new WePayUException(erro);
        }
    }

    private LocalDate[] lerPeriodo(String dataInicial, String dataFinal) throws WePayUException {
        LocalDate inicio = lerData(dataInicial, "Data inicial invalida.");
        LocalDate fim = lerData(dataFinal, "Data final invalida.");
        if (inicio.isAfter(fim)) {
            throw new WePayUException("Data inicial nao pode ser posterior aa data final.");
        }
        return new LocalDate[]{inicio, fim};
    }

    private Empregado buscarEmpregado(String id) throws WePayUException {
        if (id == null || id.isEmpty()) {
            throw new WePayUException("Identificacao do empregado nao pode ser nula.");
        }
        Empregado empregado = dados.getEmpregados().get(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    private double lerValor(String valor, String erroNulo, String erroNumerico, String erroNegativo) throws WePayUException {
        if (valor == null || valor.isEmpty()) {
            throw new WePayUException(erroNulo);
        }
        double numero;
        try {
            numero = Conversor.paraDouble(valor);
        } catch (NumberFormatException e) {
            throw new WePayUException(erroNumerico);
        }
        if (numero < 0) {
            throw new WePayUException(erroNegativo);
        }
        return numero;
    }
}
