package br.image;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import br.image.entities.EspacialProcessor;
import br.image.entities.GeometricTransformer;
import br.image.entities.Histogram;
import br.image.entities.ImageProcessor;
import br.image.entities.IntensityTransformer;
import br.image.exceptions.ImageNullException;
import br.image.gui.Alerts;

import java.io.File;

public class AppController implements Initializable {

    private ImageProcessor processetor;

    private Image imageOriginal;

    private Histogram histogram = new Histogram();

    @FXML
    private VBox vBoxSidebar;

    @FXML
    private AreaChart<Number, Number> histogramArea;

    @FXML
    private Canvas canvasImage;

    @FXML
    private Canvas canvasHist;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem menuItemLoad;

    @FXML
    private MenuItem menuItemSource;

    @FXML
    private Button buttonNegative;

    @FXML
    private Button buttonMedia;

    @FXML
    private Button buttonMedian;

    @FXML
    private Button buttonLaplacian;

    @FXML
    private Button buttonSobel;

    @FXML
    private Button buttonGray;

    @FXML
    private TextField txtFieldGamma;

    @FXML
    private TextField txtFieldBrightness;

    @FXML
    private TextField txtFieldScale;

    @FXML
    private TextField txtFieldRotation;

    @FXML
    private Slider sliderBrightness;

    @FXML
    private Slider sliderGamma;

    @FXML
    private Label labelDimImage;

    @FXML
    public void onMenuItemLoadAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Abrir imagem");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens",
                        "*.png", "*.jpg", "*.jpeg",
                        "*.bmp", "*.gif", "*.tif", "*.tiff"));

        File arquivo = chooser.showOpenDialog(App.getMainScene().getWindow());

        if (arquivo == null) {
            return;
        }

        imageOriginal = new Image(
                arquivo.toURI().toString());

        if (imageOriginal.isError() ||
                imageOriginal.getPixelReader() == null) {
            System.err.println("Não foi possível abrir a imagem.");
            return;
        }

        processetor.setImage(imageOriginal);

        imageRender(processetor.getImage());
        histogram.calcular(processetor.getImage());
        // drawHistogram();
        criarHistograma(histogram);
    }

    @FXML
    public void onMenuItemSourceAction() {
        System.out.println("teste source");

    }

    @FXML
    public void onButtonNegativeAction() {
        processetor.setIntensityTransformer(new IntensityTransformer());
        processetor.getIntensityTransformer().setImage(processetor.getImage());
        try {
            imageRender(processetor.getIntensityTransformer().Negative());
        } catch (ImageNullException e) {
            Alerts.showAlert("", "imagem nula", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onButtonApplyGamma() {
        float gamma = Float.parseFloat(txtFieldGamma.getText());
        processetor.setIntensityTransformer(new IntensityTransformer());
        processetor.getIntensityTransformer().setImage(processetor.getImage());
        imageRender(processetor.getIntensityTransformer().gammaCorrection(gamma));
    }

    @FXML
    public void onButtonMedia() {
        processetor.setEspacialProcessor(new EspacialProcessor());
        processetor.getEspacialProcessor().setImage(processetor.getImage());
        try {
            imageRender(processetor.getEspacialProcessor().mediaFilter(3, 3));
        } catch (ImageNullException e) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                    Alert.AlertType.ERROR);

        }

    }

    @FXML
    public void onButtonMedian() {
        processetor.setEspacialProcessor(new EspacialProcessor());
        processetor.getEspacialProcessor().setImage(processetor.getImage());
        try {
            imageRender(processetor.getEspacialProcessor().medianFilter(3));
        } catch (ImageNullException e) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                    Alert.AlertType.ERROR);

        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        processetor = new ImageProcessor();

        // sliderBrightness.valueProperty().addListener(new ChangeListener<Number>() {

        // @Override
        // public void changed(ObservableValue<? extends Number> num, Number oldNum,
        // Number newNum) {

        // }
        // });
        sliderGamma.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> num, Number oldNum, Number newNum) {
                txtFieldGamma.setText(String.format("%d", newNum.intValue()));
            }
        });

        txtFieldGamma.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> _obs, String oldValue, String newValue) {
                sliderGamma.setValue(Double.parseDouble(newValue));
            }
        });

        sliderGamma.setOnMouseReleased(_ -> {
            System.out.println("no reace dreg");
            float gamma = Float.parseFloat(txtFieldGamma.getText());
            gamma = gamma / 100.f;
            processetor.setIntensityTransformer(new IntensityTransformer());
            processetor.getIntensityTransformer().setImage(processetor.getImage());
            processetor.getIntensityTransformer().setImageOriginal(imageOriginal);
            try {
                imageRender(processetor.getIntensityTransformer().gammaCorrection(gamma));
            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

        sliderBrightness.valueProperty().addListener((num, oldNum, newNum) -> {
            txtFieldBrightness.setText(String.format("%d", newNum.intValue()));
        });

        txtFieldBrightness.textProperty().addListener((obs, oldText, newText) -> {
            sliderBrightness.setValue(Double.parseDouble(newText));
        });

        sliderBrightness.setOnMouseReleased(_ -> {
            int brightness = Integer.parseInt(txtFieldBrightness.getText());
            brightness *= (int) (255 / 100);
            processetor.setIntensityTransformer(new IntensityTransformer(imageOriginal));
            processetor.getIntensityTransformer().setImage(processetor.getImage());
            processetor.getIntensityTransformer().setImageOriginal(imageOriginal);

            try {
                imageRender(processetor.getIntensityTransformer().brightnessAdj(brightness));
            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

        txtFieldScale.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {

                int sx = Integer.parseInt(txtFieldScale.getText());
                int sy = sx;
                processetor.setGeometricTransformer(new GeometricTransformer());
                processetor.getGeometricTransformer().setImage(processetor.getImage());
                try {
                    imageRender(processetor.getGeometricTransformer().scale(sx, sy));
                } catch (ImageNullException e) {
                    Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                            Alert.AlertType.ERROR);

                }

            }
        });

        txtFieldRotation.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {

                int angle = Integer.parseInt(txtFieldRotation.getText());
                processetor.setGeometricTransformer(new GeometricTransformer());
                processetor.getGeometricTransformer().setImage(processetor.getImage());

                try {
                    imageRender(processetor.getGeometricTransformer().rotation(angle));
                } catch (ImageNullException e) {
                    Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                            Alert.AlertType.ERROR);
                }
            }
        });

        // buttonMedia.setOnAction(_ -> {
        // processetor.setEspacialProcessor(new EspacialProcessor());
        // processetor.getEspacialProcessor().setImage(processetor.getImage());
        // try {
        // ImageRender(processetor.getEspacialProcessor().mediaFilter(3, 3));
        // } catch (ImageNullException e) {
        // Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de
        // gama nao é valida",
        // Alert.AlertType.ERROR);

        // }

        // });
        //
        buttonLaplacian.setOnAction(_ -> {
            processetor.setEspacialProcessor(new EspacialProcessor());
            processetor.getEspacialProcessor().setImage(processetor.getImage());
            try {
                imageRender(processetor.getEspacialProcessor().laplacianFilter());
            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

        buttonSobel.setOnAction(_ -> {
            processetor.setEspacialProcessor(new EspacialProcessor());
            processetor.getEspacialProcessor().setImage(processetor.getImage());
            try {
                imageRender(processetor.getEspacialProcessor().sobelFilter());

            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

        buttonGray.setOnAction(_ -> {
            processetor.setIntensityTransformer(new IntensityTransformer());
            // processetor.getIntensityTransformer().setImageOriginal(imageOriginal);;
            processetor.getIntensityTransformer().setImage(processetor.getImage());
            try {
                imageRender(processetor.getIntensityTransformer().toGrayScale());
                histogram.calcular(processetor.getImage());
                criarHistograma(histogram);
            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

    }

    @SuppressWarnings("unchecked")
    public void criarHistograma(Histogram histograma) {

        NumberAxis eixoX = new NumberAxis(0, 255, 32);
        eixoX.setLabel("Intensidade");

        NumberAxis eixoY = new NumberAxis();
        // eixoY.setLabel("Quantidade de pixels");

        AreaChart<Number, Number> chart = new AreaChart<>(eixoX, eixoY);

        chart.setTitle("Histograma RGB");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setLegendVisible(true);

        XYChart.Series<Number, Number> serieR = criarSerie(
                "Vermelho", histograma.getVermelho());

        XYChart.Series<Number, Number> serieG = criarSerie(
                "Verde", histograma.getVerde());

        XYChart.Series<Number, Number> serieB = criarSerie(
                "Azul", histograma.getAzul());

        chart.getData().addAll(serieR, serieG, serieB);

        histogramArea = chart;
        if (vBoxSidebar.getChildren().isEmpty() == true) {

            vBoxSidebar.getChildren().addAll(chart);
        } else {
            vBoxSidebar.getChildren().remove(0);
            vBoxSidebar.getChildren().addAll(histogramArea);
        }
    }

    private XYChart.Series<Number, Number> criarSerie(
            String nome, double[] valores) {

        XYChart.Series<Number, Number> serie = new XYChart.Series<>();

        serie.setName(nome);

        for (int i = 0; i < valores.length; i++) {
            serie.getData().add(
                    new XYChart.Data<>(i, valores[i]));
        }

        return serie;
    }

    private void imageRender(Image imagem) {
        GraphicsContext gc = canvasImage.getGraphicsContext2D();

        double ch = canvasImage.getHeight();
        double cw = canvasImage.getWidth();

        gc.clearRect(0, 0, cw, ch);

        double escala = Math.min(
                cw / imagem.getWidth(),
                ch / imagem.getHeight());

        double largura = imagem.getWidth() * escala;
        double altura = imagem.getHeight() * escala;

        double x = (cw - largura) / 2;
        double y = (ch - altura) / 2;

        gc.drawImage(imagem, x, y, largura, altura);
        setDimLabel((int) imagem.getWidth(), (int) imagem.getHeight());
    }

    private void setDimLabel(int width, int height) {
        String dim = String.format("%dx%d", width, height);
        labelDimImage.setText(dim);
    }

    public void drawHistogram() {
        GraphicsContext gc = canvasHist.getGraphicsContext2D();

        double largura = canvasHist.getWidth();
        double altura = canvasHist.getHeight();

        // Margens para os eixos e os rótulos.
        double margemEsquerda = 55;
        double margemDireita = 20;
        double margemSuperior = 20;
        double margemInferior = 40;

        double graficoLargura = largura - margemEsquerda - margemDireita;
        double graficoAltura = altura - margemSuperior - margemInferior;

        double x0 = margemEsquerda;
        double y0 = margemSuperior + graficoAltura;

        gc.clearRect(0, 0, largura, altura);

        // Fundo.
        gc.setFill(Color.web("#202124"));
        gc.fillRect(0, 0, largura, altura);

        long[] r = histogram.getVermelho();
        long[] g = histogram.getVerde();
        long[] b = histogram.getAzul();

        // Maior frequência entre todos os canais.
        long max = 0;

        for (int i = 0; i < 256; i++) {
            max = Math.max(max, r[i]);
            max = Math.max(max, g[i]);
            max = Math.max(max, b[i]);
        }

        if (max == 0) {
            return;
        }

        // Grade horizontal e rótulos do eixo Y.
        gc.setFont(Font.font(10));
        gc.setLineWidth(1);
        gc.setStroke(Color.web("#44474d"));
        gc.setFill(Color.LIGHTGRAY);

        int divisõesY = 4;

        for (int i = 0; i <= divisõesY; i++) {
            double y = margemSuperior
                    + graficoAltura * i / divisõesY;

            long valor = Math.round(
                    max * (divisõesY - i) / (double) divisõesY);

            gc.strokeLine(x0, y, x0 + graficoLargura, y);
            gc.fillText(
                    String.valueOf(valor),
                    5,
                    y + 4);
        }

        // Eixos.
        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeLine(x0, margemSuperior, x0, y0);
        gc.strokeLine(x0, y0, x0 + graficoLargura, y0);

        // Rótulos do eixo X.
        int[] intensidades = { 0, 64, 128, 192, 255 };

        for (int intensidade : intensidades) {
            double x = x0 + intensidade / 255.0 * graficoLargura;

            gc.strokeLine(x, y0, x, y0 + 4);

            gc.fillText(
                    String.valueOf(intensidade),
                    x - 8,
                    y0 + 18);
        }

        // Desenha uma curva por canal.
        drawCurve(gc, r, Color.RED,
                x0, margemSuperior, graficoLargura,
                graficoAltura, max);

        drawCurve(gc, g, Color.LIME,
                x0, margemSuperior, graficoLargura,
                graficoAltura, max);

        drawCurve(gc, b, Color.DODGERBLUE,
                x0, margemSuperior, graficoLargura,
                graficoAltura, max);

        // Legenda.
        double legendaY = altura - 8;

        gc.setFill(Color.RED);
        gc.fillText("R", x0 + graficoLargura / 2 - 35, legendaY);

        gc.setFill(Color.LIME);
        gc.fillText("G", x0 + graficoLargura / 2, legendaY);

        gc.setFill(Color.DODGERBLUE);
        gc.fillText("B", x0 + graficoLargura / 2 + 35, legendaY);
    }

    private void drawCurve(
            GraphicsContext gc,
            long[] histogram,
            Color cor,
            double x0,
            double y0,
            double largura,
            double altura,
            long max) {

        gc.beginPath();
        gc.setStroke(cor);
        gc.setLineWidth(1.5);

        for (int i = 0; i < 256; i++) {
            double x = x0 + (i / 255.0) * largura;
            double y = y0 + altura
                    - (histogram[i] / (double) max) * altura;

            if (i == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }

        gc.stroke();
    }
}
