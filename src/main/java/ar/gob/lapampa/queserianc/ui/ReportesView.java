package ar.gob.lapampa.queserianc.ui;

import ar.gob.lapampa.queserianc.service.ReporteService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.IOException;
import java.nio.file.Path;

public class ReportesView extends VBox {
    private final ReporteService reporteService;
    
    public ReportesView(ReporteService reporteService) {
        this.reporteService = reporteService;
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        
        Label title = new Label("Generación de Reportes");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Formulario
        GridPane form = crearFormulario();
        Button generarBtn = new Button("Generar Reporte Diario");
        generarBtn.setOnAction(e -> generarReporte(form));
        
        Button etiquetaBtn = new Button("Generar Etiqueta Lote");
        etiquetaBtn.setOnAction(e -> generarEtiqueta(form));
        
        this.getChildren().addAll(title, form, generarBtn, etiquetaBtn);
    }
    
    private GridPane crearFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.add(new Label("Fecha:"), 0, 0);
        DatePicker fechaPicker = new DatePicker();
        fechaPicker.setValue(java.time.LocalDate.now());
        grid.add(fechaPicker, 1, 0);
        
        grid.add(new Label("Lote SENASA:"), 0, 1);
        TextField loteField = new TextField();
        loteField.setPromptText("QN-LP-20250615-001");
        grid.add(loteField, 1, 1);
        
        grid.add(new Label("Directorio salida:"), 0, 2);
        TextField dirField = new TextField();
        dirField.setText("reports/");
        Button dirBtn = new Button("...");
        dirBtn.setOnAction(e -> seleccionarDirectorio(dirField));
        HBox dirBox = new HBox(5, dirField, dirBtn);
        grid.add(dirBox, 1, 2);
        
        // Guardar referencias
        grid.getProperties().put("fechaPicker", fechaPicker);
        grid.getProperties().put("loteField", loteField);
        grid.getProperties().put("dirField", dirField);
        
        return grid;
    }
    
    private void seleccionarDirectorio(TextField dirField) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar directorio de salida");
        var directorio = chooser.showDialog(null);
        if (directorio != null) {
            dirField.setText(directorio.getAbsolutePath());
        }
    }
    
    private void generarReporte(GridPane form) {
        try {
            DatePicker fechaPicker = (DatePicker) form.getProperties().get("fechaPicker");
            TextField dirField = (TextField) form.getProperties().get("dirField");
            
            java.time.LocalDate fecha = fechaPicker.getValue();
            String directorio = dirField.getText();
            
            // Crear directorio si no existe
            java.nio.file.Files.createDirectories(Path.of(directorio));
            
            String ruta = directorio + "/reporte_" + fecha.toString() + ".pdf";
            reporteService.generarReporteDiario(ruta, fecha);
            
            mostrarExito("Reporte generado", "Reporte guardado en:\n" + ruta);
            
        } catch (IOException e) {
            showError("Error", "No se pudo generar el reporte: " + e.getMessage());
        } catch (Exception e) {
            showError("Error", "Error al generar reporte: " + e.getMessage());
        }
    }
    
    private void generarEtiqueta(GridPane form) {
        try {
            TextField loteField = (TextField) form.getProperties().get("loteField");
            TextField dirField = (TextField) form.getProperties().get("dirField");
            
            String lote = loteField.getText().trim();
            String directorio = dirField.getText();
            
            if (lote.isEmpty()) {
                throw new IllegalArgumentException("Ingrese un código de lote");
            }
            
            // Crear directorio si no existe
            java.nio.file.Files.createDirectories(Path.of(directorio));
            
            String ruta = directorio + "/etiqueta_" + lote + ".pdf";
            reporteService.generarEtiquetaLote(ruta, lote);
            
            mostrarExito("Etiqueta generada", "Etiqueta guardada en:\n" + ruta);
            
        } catch (IOException e) {
            showError("Error", "No se pudo generar la etiqueta: " + e.getMessage());
        } catch (Exception e) {
            showError("Error", "Error al generar etiqueta: " + e.getMessage());
        }
    }
    
    private void mostrarExito(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
