package br.image;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import br.image.entities.EspacialProcessor;
import br.image.entities.GeometricTransformer;
import br.image.entities.Histogram;
import br.image.entities.ImageProcessor;
import br.image.entities.IntensityTransformer;
import br.image.exceptions.ImageNullException;
import br.image.gui.Alerts;
import br.image.utils.BrushTool;
import br.image.utils.DFT2D;
import br.image.utils.FFT2D;
import br.image.utils.HSIColorWheel;
import br.image.utils.HSIConverter;
import br.image.utils.Utils;
import br.image.utils.FFT2D.Spectrum;

import java.io.File;
import java.io.IOException;

public class AppController implements Initializable {

    private ImageProcessor processetor;

    private Image imageOriginal;

    private Histogram histogram = new Histogram();

    private BrushTool brush;

    private Spectrum currentSpectrum;

    private double renderX, renderWidth, renderY, renderHeight;

    @FXML
    private VBox vBoxSidebar;

    @FXML
    private AreaChart<Number, Number> histogramArea;

    @FXML
    private Canvas canvasImage;

    @FXML
    private Canvas canvasHist;

    @FXML
    private Canvas canvasFourier;

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
    private Button buttonEquaHist;

    @FXML
    private Button buttonFFT;

    @FXML
    private Button buttonFuorrieTr;

    @FXML
    private Button buttonFFTI;

    @FXML
    private Button buttonCreateKernel;

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
    private TabPane tabPane;

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

        imageRender(processetor.getImage(), canvasImage, false);
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
            imageRender(processetor.getIntensityTransformer().Negative(), canvasImage, false);
        } catch (ImageNullException e) {
            Alerts.showAlert("", "imagem nula", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onButtonApplyGamma() {
        float gamma = Float.parseFloat(txtFieldGamma.getText());
        processetor.setIntensityTransformer(new IntensityTransformer());
        processetor.getIntensityTransformer().setImage(processetor.getImage());
        imageRender(processetor.getIntensityTransformer().gammaCorrection(gamma), canvasImage, false);
    }

    @FXML
    public void onButtonMedia() {
        processetor.setEspacialProcessor(new EspacialProcessor());
        processetor.getEspacialProcessor().setImage(processetor.getImage());
        try {
            imageRender(processetor.getEspacialProcessor().mediaFilter(3, 3), canvasImage, false);
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
            imageRender(processetor.getEspacialProcessor().medianFilter(3), canvasImage, false);
        } catch (ImageNullException e) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                    Alert.AlertType.ERROR);

        }
    }

    public ImageProcessor getProcessetor() {
        return processetor;
    }

    public Canvas getCanvasImage() {
        return canvasImage;
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
        brush = new BrushTool(canvasFourier);
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
                imageRender(processetor.getIntensityTransformer().gammaCorrection(gamma), canvasImage, false);
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
                imageRender(processetor.getIntensityTransformer().brightnessAdj(brightness), canvasImage, false);
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
                    imageRender(processetor.getGeometricTransformer().scale(sx, sy), canvasImage, false);
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
                    imageRender(processetor.getGeometricTransformer().rotation(angle), canvasImage, false);
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
                imageRender(processetor.getEspacialProcessor().laplacianFilter(), canvasImage, false);
            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

        buttonSobel.setOnAction(_ -> {
            processetor.setEspacialProcessor(new EspacialProcessor());
            processetor.getEspacialProcessor().setImage(processetor.getImage());
            try {
                imageRender(processetor.getEspacialProcessor().sobelFilter(), canvasImage, false);

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
                imageRender(processetor.getIntensityTransformer().toGrayScale(), canvasImage, false);
                histogram.calcular(processetor.getImage());
                criarHistograma(histogram);
            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                        Alert.AlertType.ERROR);
            }
        });

        buttonEquaHist.setOnAction(_ -> {
            try {
                histogram.calcular(processetor.getImage());
                imageRender(histogram.histogramEqualization(processetor.getImage()), canvasImage, false);
                criarHistograma(histogram);

            } catch (ImageNullException e) {
                Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao degama nao é valida",
                        Alert.AlertType.ERROR);

            }
        });

        buttonFFT.setOnAction(_ -> {
            currentSpectrum = FFT2D.transform(processetor.getImage());
            imageRender(currentSpectrum.spectrumImage, canvasFourier, true);
        });

        buttonFuorrieTr.setOnAction(_ -> {
            imageRender(DFT2D.transform(processetor.getImage()).spectrumImage, canvasFourier, true);
        });

        buttonFFTI.setOnAction(_ -> {
            applyIFFT();
        });

        buttonCreateKernel.setOnAction(event -> {
            createDialogFormKernelCreate(Utils.currentStage(event));
        });

        Tab tab = tabPane.getTabs().get(0);
        ScrollPane scrol = (ScrollPane) tab.getContent();
        VBox hsiArea = createHSIPanel();
        AnchorPane ancor = (AnchorPane) scrol.getContent();
        ancor.getChildren().add(hsiArea);

        AnchorPane.setTopAnchor(hsiArea, 300.0);
        AnchorPane.setLeftAnchor(hsiArea, 20.0);

    }

    private void createDialogFormKernelCreate(Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createKernelForm.fxml"));
            Pane pane = loader.load();

            Stage formsStage = new Stage();
            formsStage.setTitle("Crie kernel");
            formsStage.setScene(new Scene(pane));
            formsStage.setResizable(false);
            formsStage.initOwner(parentStage);
            formsStage.initModality(Modality.WINDOW_MODAL);
            formsStage.showAndWait();

        } catch (Exception e) {
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private double[][] getMask() {

        int width = currentSpectrum.width;
        int height = currentSpectrum.height;

        WritableImage snapshot = canvasFourier.snapshot(
                null,
                null);

        PixelReader reader = snapshot.getPixelReader();

        double[][] mask = new double[height][width];

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                double screenX = renderX +
                        (x + 0.5) * renderWidth / width;

                double screenY = renderY +
                        (y + 0.5) * renderHeight / height;

                int px = (int) screenX;
                int py = (int) screenY;

                if (px < 0 || py < 0 ||
                        px >= snapshot.getWidth() ||
                        py >= snapshot.getHeight()) {

                    mask[y][x] = 0;
                    continue;
                }

                Color color = reader.getColor(px, py);

                // Considera também a transparência
                mask[y][x] = color.getBrightness() * color.getOpacity();
            }
        }

        return mask;
    }

    private VBox createHSIPanel() {

        HSIColorWheel colorWheel = new HSIColorWheel(280);

        Label hueLabel = new Label("Hue: 0°");
        Label saturationLabel = new Label("Saturation: 0.00");
        Label intensityLabel = new Label("Intensity: 0.50");

        Label rgbLabel = new Label("RGB: (128, 128, 128)");

        Rectangle colorPreview = new Rectangle(100, 60);
        colorPreview.setFill(Color.gray(0.5));
        colorPreview.setArcWidth(10);
        colorPreview.setArcHeight(10);

        Slider intensitySlider = new Slider(0, 1, 0.5);

        intensitySlider.setShowTickLabels(true);
        intensitySlider.setShowTickMarks(true);
        intensitySlider.setMajorTickUnit(0.25);

        intensitySlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    colorWheel.setIntensity(newValue.doubleValue());
                });

        colorWheel.setOnSelectionChanged(hsi -> {

            hueLabel.setText(
                    String.format("Hue: %.2f°", hsi.h));

            saturationLabel.setText(
                    String.format("Saturation: %.2f", hsi.s));

            intensityLabel.setText(
                    String.format("Intensity: %.2f", hsi.i));

            // Converte HSI selecionado para RGB
            Color rgb = HSIConverter.hsiToRGB(hsi);

            // Atualiza a prévia
            colorPreview.setFill(rgb);

            // RGB de 0 a 255
            int r = (int) Math.round(rgb.getRed() * 255);
            int g = (int) Math.round(rgb.getGreen() * 255);
            int b = (int) Math.round(rgb.getBlue() * 255);

            rgbLabel.setText(
                    String.format("RGB: (%d, %d, %d)", r, g, b));
        });

        VBox panel = new VBox(
                12,
                new Label("HSI Color Selector"),
                colorWheel,
                hueLabel,
                saturationLabel,
                intensityLabel,
                new Label("Intensity"),
                intensitySlider,
                new Label("Selected RGB"),
                colorPreview,
                rgbLabel);

        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15));

        return panel;
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
                "Vermelho", histograma.getRed());

        XYChart.Series<Number, Number> serieG = criarSerie(
                "Verde", histograma.getGreen());

        XYChart.Series<Number, Number> serieB = criarSerie(
                "Azul", histograma.getBlue());

        chart.getData().addAll(serieR, serieG, serieB);

        histogramArea = chart;
        if (vBoxSidebar.getChildren().isEmpty() == true
                | vBoxSidebar.getChildren().get(0).getClass() != AreaChart.class) {

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

    public void imageRender(Image imagem, Canvas canvas, boolean frequencyRender) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double ch = canvas.getHeight();
        double cw = canvas.getWidth();

        gc.clearRect(0, 0, cw, ch);

        double escala = Math.min(
                cw / imagem.getWidth(),
                ch / imagem.getHeight());

        double largura = imagem.getWidth() * escala;
        double altura = imagem.getHeight() * escala;

        double x = (cw - largura) / 2;
        double y = (ch - altura) / 2;

        if (frequencyRender) {

            // Dimensões finais da imagem renderizada
            renderWidth = largura;
            renderHeight = altura;

            // Posição da imagem centralizada
            renderX = (cw - renderWidth) / 2;
            renderY = (ch - renderHeight) / 2;
        }
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

        double[] r = histogram.getRed();
        double[] g = histogram.getGreen();
        double[] b = histogram.getBlue();

        // Maior frequência entre todos os canais.
        double max = 0;

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
            double[] histogram,
            Color cor,
            double x0,
            double y0,
            double largura,
            double altura,
            double max) {

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

    private void applyIFFT() {

        if (currentSpectrum == null) {
            System.out.println("Execute a FFT primeiro.");
            return;
        }

        // Obtém a máscara desenhada
        double[][] mask = getMask();

        // Executa a transformada inversa filtrada
        WritableImage result = FFT2D.inverseFiltered(
                currentSpectrum,
                mask);

        // Remove a camada de desenho
        brush.clear();

        // Renderiza a imagem reconstruída
        imageRender(result, canvasImage, false);
    }

}
