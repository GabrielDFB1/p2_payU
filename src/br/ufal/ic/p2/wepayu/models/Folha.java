package br.ufal.ic.p2.wepayu.models;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.ufal.ic.p2.wepayu.Exception.AgendaPagamentoException;
import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;
import br.ufal.ic.p2.wepayu.Exception.ComandoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNomeNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoNulaException;
import br.ufal.ic.p2.wepayu.Exception.MembroSindicatoException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.DataUtil;
import br.ufal.ic.p2.wepayu.utils.Intervalo;
import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public class Folha {
	private Map<String, Empregado> empregados;
	private List<String> agendas;
	private Historico historico = new Historico();
	private boolean encerrado = false;

	@FunctionalInterface
	private interface Comando<T> {
		T executar() throws WePayUException;
	}

	public Folha() {
		DadosSistema dados;
		try {
			dados = Persistencia.carregar();
		} catch (Exception e) {
			dados = new DadosSistema();
		}
		usarDados(dados);
	}

	public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws WePayUException {
		return registrarComando(() -> {
			if (tipo.equals("horista")) {
				return adicionar(new Horista(nome, endereco, salario));
			}
			if (tipo.equals("assalariado")) {
				return adicionar(new Assalariado(nome, endereco, salario));
			}
			if (tipo.equals("comissionado")) {
				throw new AtributoInvalidoEmpregadoException("Tipo nao aplicavel.");
			}
			throw new AtributoInvalidoEmpregadoException("Tipo invalido.");
		});
	}

	public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
			throws WePayUException {
		return registrarComando(() -> {
			if (tipo.equals("comissionado")) {
				return adicionar(new Comissionado(nome, endereco, salario, comissao));
			}
			if (tipo.equals("horista") || tipo.equals("assalariado")) {
				throw new AtributoInvalidoEmpregadoException("Tipo nao aplicavel.");
			}
			throw new AtributoInvalidoEmpregadoException("Tipo invalido.");
		});
	}

	private String adicionar(Empregado empregado) {
		String id = UUID.randomUUID().toString();
		empregados.put(id, empregado);
		return id;
	}

	public String getAtributoEmpregado(String empregado, String atributo) throws WePayUException {
		return buscarEmpregado(empregado).getAtributo(atributo);
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

	public String getEmpregadoPorNome(String nome, String indiceTexto) throws EmpregadoNomeNaoExisteException {
		int indice = Integer.parseInt(indiceTexto);
		int contador = 1;
		for (Map.Entry<String, Empregado> empregado : empregados.entrySet()) {
			if (empregado.getValue().getNome().contains(nome)) {
				if (contador == indice) {
					return empregado.getKey();
				}
				contador++;
			}
		}
		throw new EmpregadoNomeNaoExisteException("Nao ha empregado com esse nome.");
	}

	public String getNumeroDeEmpregados() {
		return String.valueOf(empregados.size());
	}

	public void removerEmpregado(String id) throws WePayUException {
		registrarComando(() -> {
			buscarEmpregado(id);
			empregados.remove(id);
			return null;
		});
	}

	public void lancaCartao(String emp, String data, String horas) throws WePayUException {
		registrarComando(() -> {
			buscarEmpregado(emp).lancaCartao(data, horas);
			return null;
		});
	}

	public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
		return buscarEmpregado(emp).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
	}

	public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
		return buscarEmpregado(emp).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
	}

	public void lancaVenda(String emp, String data, String valor) throws WePayUException {
		registrarComando(() -> {
			buscarEmpregado(emp).lancaVenda(data, valor);
			return null;
		});
	}

	public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
		return buscarEmpregado(emp).getVendasRealizadas(dataInicial, dataFinal);
	}

	public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
		registrarComando(() -> {
			if (membro == null || membro.isBlank()) {
				throw new MembroSindicatoException("Identificacao do membro nao pode ser nula.");
			}
			Empregado empregado = buscarPorMembro(membro);
			if (empregado == null) {
				throw new MembroSindicatoException("Membro nao existe.");
			}
			empregado.getMembroSindicato().lancaTaxaServico(data, valor);
			return null;
		});
	}

	public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
		return buscarEmpregado(emp).getTaxasServico(dataInicial, dataFinal);
	}

	private Empregado buscarPorMembro(String idMembro) {
		for (Empregado empregado : empregados.values()) {
			MembroSindicato membro = empregado.getMembroSindicato();
			if (membro != null && membro.getIdMembro().equals(idMembro)) {
				return empregado;
			}
		}
		return null;
	}

	public void alteraEmpregado(String emp, String atributo, String valor) throws WePayUException {
		registrarComando(() -> {
			Empregado empregado = buscarEmpregado(emp);
			switch (atributo) {
			case "tipo":
				mudarTipo(emp, empregado, valor, null);
				break;
			case "sindicalizado":
				if (valor.equals("false")) {
					empregado.setMembroSindicato(null);
				} else if (valor.equals("true")) {
					throw new MembroSindicatoException("Identificacao do sindicato nao pode ser nula.");
				} else {
					throw new ValorInvalidoException("Valor deve ser true ou false.");
				}
				break;
			case "agendaPagamento":
				if (!agendas.contains(valor)) {
					throw new AgendaPagamentoException("Agenda de pagamento nao esta disponivel");
				}
				empregado.setAgendaPagamento(valor);
				break;
			default:
				empregado.alterarAtributo(atributo, valor);
			}
			return null;
		});
	}

	public void alteraEmpregado(String emp, String atributo, String valor, String extra) throws WePayUException {
		registrarComando(() -> {
			Empregado empregado = buscarEmpregado(emp);
			if (!atributo.equals("tipo")) {
				throw new AtributoInvalidoEmpregadoException("Atributo nao existe.");
			}
			mudarTipo(emp, empregado, valor, extra);
			return null;
		});
	}

	public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
			throws WePayUException {
		registrarComando(() -> {
			Empregado empregado = buscarEmpregado(emp);
			if (!atributo.equals("sindicalizado")) {
				throw new AtributoInvalidoEmpregadoException("Atributo nao existe.");
			}
			if (valor.equals("false")) {
				empregado.setMembroSindicato(null);
				return null;
			}
			if (!valor.equals("true")) {
				throw new ValorInvalidoException("Valor deve ser true ou false.");
			}
			if (idSindicato == null || idSindicato.isBlank()) {
				throw new MembroSindicatoException("Identificacao do sindicato nao pode ser nula.");
			}
			double taxa = NumeroUtil.converterNaoNegativo(taxaSindical, "Taxa sindical nao pode ser nula.",
					"Taxa sindical deve ser numerica.", "Taxa sindical deve ser nao-negativa.");
			Empregado outro = buscarPorMembro(idSindicato);
			if (outro != null && outro != empregado) {
				throw new MembroSindicatoException("Ha outro empregado com esta identificacao de sindicato");
			}
			empregado.setMembroSindicato(new MembroSindicato(idSindicato, taxa));
			return null;
		});
	}

	public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia,
			String contaCorrente) throws WePayUException {
		registrarComando(() -> {
			Empregado empregado = buscarEmpregado(emp);
			if (!atributo.equals("metodoPagamento")) {
				throw new AtributoInvalidoEmpregadoException("Atributo nao existe.");
			}
			if (!valor.equals("banco")) {
				empregado.alterarAtributo(atributo, valor);
				return null;
			}
			if (banco == null || banco.isBlank()) {
				throw new ValorInvalidoException("Banco nao pode ser nulo.");
			}
			if (agencia == null || agencia.isBlank()) {
				throw new ValorInvalidoException("Agencia nao pode ser nulo.");
			}
			if (contaCorrente == null || contaCorrente.isBlank()) {
				throw new ValorInvalidoException("Conta corrente nao pode ser nulo.");
			}
			empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
			return null;
		});
	}

	private void mudarTipo(String id, Empregado antigo, String tipo, String extra) throws WePayUException {
		Empregado novo;
		if (tipo.equals("horista")) {
			novo = new Horista();
		} else if (tipo.equals("assalariado")) {
			novo = new Assalariado();
		} else if (tipo.equals("comissionado")) {
			if (extra == null) {
				throw new AtributoInvalidoEmpregadoException("Tipo nao aplicavel.");
			}
			Comissionado comissionado = new Comissionado();
			comissionado.setComissao(Comissionado.converterComissao(extra));
			novo = comissionado;
		} else {
			throw new AtributoInvalidoEmpregadoException("Tipo invalido.");
		}
		double novoSalario = antigo.getSalario();
		if (extra != null && !tipo.equals("comissionado")) {
			novoSalario = Empregado.converterSalario(extra);
		}

		novo.copiarDadosDe(antigo);
		novo.setSalario(novoSalario);
		novo.setAgendaPagamento(novo.getAgendaPadrao());
		empregados.put(id, novo);
	}

	public String totalFolha(String data) throws WePayUException {
		LocalDate dia = DataUtil.converter(data, "Data invalida.");
		BigDecimal total = BigDecimal.ZERO;
		for (Contracheque c : calcularContracheques(dia)) {
			total = total.add(c.getBruto());
		}
		return NumeroUtil.formatarDinheiro(total);
	}

	public void rodaFolha(String data, String saida) throws WePayUException {
		registrarComando(() -> {
			LocalDate dia = DataUtil.converter(data, "Data invalida.");
			List<Contracheque> horistas = new ArrayList<>();
			List<Contracheque> assalariados = new ArrayList<>();
			List<Contracheque> comissionados = new ArrayList<>();
			BigDecimal total = BigDecimal.ZERO;

			for (Contracheque c : calcularContracheques(dia)) {
				aplicarDescontos(c, dia);
				total = total.add(c.getBruto());
				switch (c.getEmpregado().getTipo()) {
				case "horista":
					horistas.add(c);
					break;
				case "assalariado":
					assalariados.add(c);
					break;
				default:
					comissionados.add(c);
				}
			}
			try {
				RelatorioFolha.gerar(saida, dia, horistas, assalariados, comissionados, total);
			} catch (IOException e) {
				throw new WePayUException("Nao foi possivel gravar o arquivo " + saida);
			}
			return null;
		});
	}

	private List<Contracheque> calcularContracheques(LocalDate dia) throws AgendaPagamentoException {
		List<Contracheque> lista = new ArrayList<>();
		for (Empregado empregado : empregados.values()) {
			AgendaPagamento agenda = AgendaPagamento.de(empregado.getAgendaPagamento());
			if (agenda.ehDiaDePagamento(dia)) {
				lista.add(empregado.calcularPagamento(dia, agenda));
			}
		}
		lista.sort(Comparator.comparing(c -> c.getEmpregado().getNome()));
		return lista;
	}

	private void aplicarDescontos(Contracheque c, LocalDate dia) throws AgendaPagamentoException {
		Empregado empregado = c.getEmpregado();
		MembroSindicato membro = empregado.getMembroSindicato();
		if (membro == null) {
			return;
		}
		AgendaPagamento agenda = AgendaPagamento.de(empregado.getAgendaPagamento());
		LocalDate anterior = agenda.pagamentoAnterior(dia);
		long dias = ChronoUnit.DAYS.between(anterior, dia);
		BigDecimal devido = membro.descontosDevidos(Intervalo.periodoDePagamento(anterior, dia), dias);

		BigDecimal cobrado = devido.min(c.getBruto());
		c.setDescontos(cobrado);
		membro.setDivida(devido.subtract(cobrado).doubleValue());
	}

	public void criarAgendaDePagamentos(String descricao) throws WePayUException {
		registrarComando(() -> {
			AgendaPagamento.de(descricao);
			if (agendas.contains(descricao)) {
				throw new AgendaPagamentoException("Agenda de pagamentos ja existe");
			}
			agendas.add(descricao);
			return null;
		});
	}

	public void zerar() throws WePayUException {
		registrarComando(() -> {
			usarDados(new DadosSistema());
			return null;
		});
	}

	public void encerrarSistema() throws IOException {
		Persistencia.salvar(dadosAtuais());
		encerrado = true;
	}

	public void undo() throws WePayUException {
		verificarEncerrado();
		importarEstado(historico.desfazer(exportarEstado()));
	}

	public void redo() throws WePayUException {
		verificarEncerrado();
		importarEstado(historico.refazer(exportarEstado()));
	}

	private void verificarEncerrado() throws ComandoException {
		if (encerrado) {
			throw new ComandoException("Nao pode dar comandos depois de encerrarSistema.");
		}
	}

	private <T> T registrarComando(Comando<T> comando) throws WePayUException {
		String estadoAnterior = exportarEstado();
		T resultado = comando.executar();
		historico.registrar(estadoAnterior);
		return resultado;
	}

	private String exportarEstado() {
		return Persistencia.paraXml(dadosAtuais());
	}

	private void importarEstado(String estado) {
		usarDados(Persistencia.deXml(estado));
	}

	private DadosSistema dadosAtuais() {
		DadosSistema dados = new DadosSistema();
		dados.setEmpregados(empregados);
		dados.setAgendas(agendas);
		return dados;
	}

	private void usarDados(DadosSistema dados) {
		this.empregados = dados.getEmpregados();
		this.agendas = dados.getAgendas();
	}
}
