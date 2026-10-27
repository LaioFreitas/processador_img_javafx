package br.image.entities;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import br.image.gui.Alerts;

public class ImageLoader {
    private static File imageLoaded;

    public static Image OpenImage(Window mainWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Abrir imagem");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens",
                        "*.png", "*.jpg", "*.jpeg",
                        "*.bmp", "*.gif", "*.tif", "*.tiff"));

        imageLoaded = chooser.showOpenDialog(mainWindow);

        if (imageLoaded == null) {
            Alerts.showAlert("Load error", "Error a carregar a imagem", "Arquivo vazil", Alert.AlertType.ERROR);
        }

        Image image = new Image(
                imageLoaded.toURI().toString());

        if (image.isError() ||
                image.getPixelReader() == null) {
            System.err.println("Não foi possível abrir a imagem.");
            Alerts.showAlert("Load error", "Error a carregar a imagem", "Error no carregamento da imagem",
                    Alert.AlertType.ERROR);
        }
        return image;
    }
}
