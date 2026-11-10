package br.image;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

import br.image.entities.EspacialProcessor;
import br.image.gui.Alerts;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class FormCreatKernelController implements Initializable {

    double kernelTam;
    double[][] kernel;

    @FXML
    private TextField textTam;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button buttonOK;

    @FXML
    private Button buttonCancel;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        textTam.textProperty().addListener((obs, oldText, newText) -> {
            kernelTam = Double.parseDouble(newText);
            for (int i = 0; i < kernelTam; i++) {
                for (int j = 0; j < kernelTam; j++) {
                    gridPane.add(new TextField(), i, j);
                }
            }
        });

        buttonOK.setOnAction(_ -> {
            ObservableList<Node> filds = gridPane.getChildren();
            kernel = new double[(int) kernelTam][(int) kernelTam];
            Queue<Double> list = new ArrayDeque<>();
            for (Node node : filds) {
                list.add(Double.parseDouble(((TextField) node).getText()));
            }
            for (int i = 0; i < kernelTam; i++) {
                for (int j = 0; j < kernelTam; j++) {
                    kernel[i][j] = list.poll();
                }
            }

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
                Pane pane = loader.load();

                AppController controller = loader.getController();

                controller.getProcessetor().setEspacialProcessor(new EspacialProcessor());
                controller.getProcessetor().getEspacialProcessor().setImage(controller.getProcessetor().getImage());
                try {
                    controller.imageRender(controller.getProcessetor().getEspacialProcessor().applyKernel(kernel),
                            controller.getCanvasImage(), false);

                } catch (Exception e) {
                    Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                            AlertType.ERROR);
                }

            } catch (Exception e) {
                Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);

            }

        });

    }

}
