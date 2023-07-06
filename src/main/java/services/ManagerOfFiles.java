package services;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManagerOfFiles {

	public String launchExplorer() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Sélectionnez un fichier CBR");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CBR", "cbr"));

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String filePath = selectedFile.getAbsolutePath();

			return filePath;
		} else {
			System.out.println("Aucun fichier sélectionné");
			return null;
		}
	}
	
	public File stockageExplorer() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner un répertoire pour stocker le resultat");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
	}
	
	public File creationRepertoireTemporaire() {
		 File[] roots = File.listRoots();

	        for (File root : roots) {
	            String chemin = root.getAbsolutePath() + "temp";
	            File repertoireTemp = new File(chemin);

	            if (!repertoireTemp.exists()) {
	                if (repertoireTemp.mkdir()) {
	                    System.out.println("Répertoire temp créé d: " + repertoireTemp.getAbsolutePath());
	                    return repertoireTemp;
	                } else {
	                    System.out.println("Impossible de créer le répertoire temp.");
	                }
	            } else {
	                System.out.println("Le répertoire temp existe déjà : " + repertoireTemp.getAbsolutePath());
	                return repertoireTemp;
	            }
	        }
	        System.out.println("Aucun disque disponible pour créer le répertoire temp.");   
	        return null;
	}
	
	public void suppressionRepertoireTemporaire(File repertoire) {
		if (repertoire.exists()) {
            if (repertoire.isDirectory()) {
                File[] fichiers = repertoire.listFiles();
                if (fichiers != null) {
                    for (File fichier : fichiers) {
                        fichier.delete();
                    }
                }
            }

            if (repertoire.delete()) {
                System.out.println("Répertoire supprimé : " + repertoire.getAbsolutePath());
            } else {
                System.out.println("Impossible de supprimer le répertoire : " + repertoire.getAbsolutePath());
            }
        } else {
            System.out.println("Le répertoire n'existe pas : " + repertoire.getAbsolutePath());
        }
	}
	
}
