import easyaccept.EasyAccept;

public class Main {
	public static void main(String[] args) {
		String facade = "br.ufal.ic.p2.wepayu.Facade";
		String[] testes = {
				"us1", "us1_1", "us2", "us2_1", "us3", "us3_1", "us4", "us4_1",
				"us5", "us5_1", "us6", "us6_1", "us7", "us8", "us9", "us9_1", "us10", "us10_1" };
		for (String teste : testes) {
			EasyAccept.main(new String[] { facade, "tests/" + teste + ".txt" });
		}
	}
}
