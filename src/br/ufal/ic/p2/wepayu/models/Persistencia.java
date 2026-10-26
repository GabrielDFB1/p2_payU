package br.ufal.ic.p2.wepayu.models;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Persistencia {
	private static final String ARQUIVO = "folha.xml";

	public static void salvar(DadosSistema dados) throws IOException {
		try (XMLEncoder encoder = new XMLEncoder(
				new BufferedOutputStream(new FileOutputStream(ARQUIVO)))) {
			encoder.writeObject(dados);
		}
	}

	public static DadosSistema carregar() throws IOException {
		File arquivo = new File(ARQUIVO);
		if (!arquivo.exists()) {
			return new DadosSistema();
		}
		try (XMLDecoder decoder = new XMLDecoder(
				new BufferedInputStream(new FileInputStream(arquivo)))) {
			return (DadosSistema) decoder.readObject();
		}
	}

	public static String paraXml(DadosSistema dados) {
		ByteArrayOutputStream saida = new ByteArrayOutputStream();
		try (XMLEncoder encoder = new XMLEncoder(saida)) {
			encoder.writeObject(dados);
		}
		return saida.toString(StandardCharsets.UTF_8);
	}

	public static DadosSistema deXml(String xml) {
		try (XMLDecoder decoder = new XMLDecoder(
				new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))) {
			return (DadosSistema) decoder.readObject();
		}
	}
}
