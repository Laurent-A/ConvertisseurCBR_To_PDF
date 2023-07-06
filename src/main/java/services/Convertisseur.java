package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Convertisseur {
	
	ManagerOfFiles managerOfFiles = new ManagerOfFiles();
	File pathTemporaire = null;
	String destinationFolderPath = null;

	public void convertToPdf() {
		
		if(demarrage()) {
			String cheminFichierCbrFromLauncher = managerOfFiles.launchExplorer();
			extraireImagesDuCbr(cheminFichierCbrFromLauncher);
			creationDuPdf(cheminFichierCbrFromLauncher, recupererNumeroDeTome(cheminFichierCbrFromLauncher));
			
			managerOfFiles.suppressionRepertoireTemporaire(pathTemporaire);
			afficherDialogResultat(destinationFolderPath);
		} else {
			afficherDialogExit();
		}

	}

	private void extraireImagesDuCbr(String cheminFichierCbr) {
		String zipFilePath = cheminFichierCbr;
		pathTemporaire = managerOfFiles.creationRepertoireTemporaire();
		destinationFolderPath = managerOfFiles.stockageExplorer().getPath();

		try {
			File destinationFolder = new File(pathTemporaire.getPath());
			if (!destinationFolder.exists()) {
				destinationFolder.mkdir();
			}

			FileInputStream fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);

			ZipEntry entry = zis.getNextEntry();
			while (entry != null) {
				String entryName = entry.getName();
				if (entryName.endsWith(".jpg") || entryName.endsWith(".png")) {
					String filePath = pathTemporaire + File.separator + entryName;
					FileOutputStream fos = new FileOutputStream(filePath);

					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = zis.read(buffer)) != -1) {
						fos.write(buffer, 0, bytesRead);
					}

					fos.close();
				}
				zis.closeEntry();
				entry = zis.getNextEntry();
			}

			zis.close();
			fis.close();

			System.out.println("Extraction terminée avec succès.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private List<File> recupererImageDuDossier(String cheminFichierPdf) {
		List<File> imageList = new ArrayList<File>();

		File folder = new File(pathTemporaire.getPath());
		if (folder.exists() && folder.isDirectory()) {
			String[] extensions = { "jpg", "png" };
			imageList = (List<File>) FileUtils.listFiles(folder, extensions, true);
			return imageList;
		} else {
			return null;
		}
	}

	private void creationDuPdf(String cheminFichierPdf, String numero) {

		List<File> imageList = recupererImageDuDossier(cheminFichierPdf);

		String valeur = "CLN_Tome_";
		String resultat = valeur + numero + ".pdf";
		String outputFilePath = destinationFolderPath + File.separator + resultat;

		try {
			PDDocument document = new PDDocument();
			
			for (File imageFile : imageList) {
				PDImageXObject image = PDImageXObject.createFromFileByContent(imageFile, document);
				PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
				document.addPage(page);
				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				contentStream.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
				contentStream.close();
			}

			document.save(outputFilePath);
			document.close();

			System.out.println(
					"Conversion en PDF terminée avec succès. Le fichier PDF est enregistré dans le dossier cible.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String recupererNumeroDeTome(String path) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(path);
		StringBuilder chiffres = new StringBuilder();

		while (matcher.find()) {
			chiffres.append(matcher.group());
		}

		return chiffres.toString();
	}
	
	public boolean demarrage() {
		int choix = JOptionPane.showOptionDialog(
                null,
                "Merci d'avoir choisi notre service de conversion ! Souhaitez-vous convertir votre fichier .CBR en .PDF ?",
                "Confirmation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                JOptionPane.OK_OPTION);

        if (choix == JOptionPane.OK_OPTION) {
            return true;
        } else if (choix == JOptionPane.CANCEL_OPTION || choix == JOptionPane.CLOSED_OPTION) {
        	return false;
        }
        return false;
	}
	
    public void afficherDialogExit() {
        String message = "Au revoir";

        JOptionPane.showMessageDialog(null, message);
    }
    
    public void afficherDialogResultat(String destination) {
        String message = "Vous pouvez récuperer votre document dans le répertoire suivant : " + destination;

        JOptionPane.showMessageDialog(null, message);
    }
}
