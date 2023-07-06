package app;

import services.Convertisseur;

public class MainApp {

	public static void main(String[] args) throws Exception {

		Convertisseur convertisseur = new Convertisseur();

		convertisseur.convertToPdf();

	}

}
