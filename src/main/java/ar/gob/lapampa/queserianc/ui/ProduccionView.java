package ar.gob.lapampa.queserianc.ui;

import ar.gob.lapampa.queserianc.model.Queso;
import ar.gob.lapampa.queserianc.service.ProduccionService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ProduccionView extends VBox {
    private final ProduccionService produccionService;
    
    public ProduccionView(ProduccionService produccionService) {
        this.produccionService = produccionService;
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        
        Label title = new Label("Registro de Producción Diaria");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane form = crearFormulario();
        Button guardarBtn = new Button("Registrar Queso");
        guardarBtn.setOnAction(e -> registrarQueso(form));
        
        this.getChildren().addAll(title, form, guardarBtn);
    }
    
    private GridPane crearFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        // Tipo de queso
        grid.add(new Label("Tipo de queso:"), 0, 0);
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("criollo", "sardo", "provolone", "quesillo");
        tipoCombo.setValue("criollo");
        grid.add(tipoCombo, 1, 0);
        
        // Litros de leche
        grid.add(new Label("Litros de leche:"), 0, 1);
        TextField litrosField = new TextField();
        litrosField.setText("20");
        grid.add(litrosField, 1, 1);
        
        // Peso final
        grid.add(new Label("Peso final (kg):"), 0, 2);
        TextField pesoField = new TextField();
        pesoField.setText("2");
        grid.add(pesoField, 1, 2);
        
        // Guardar referencias para usar en registrarQueso()
        grid.getProperties().put("tipoCombo", tipoCombo);
        grid.getProperties().put("litrosField", litrosField);
        grid.getProperties().put("pesoField", pesoField);
        
        return grid;
    }
    
    private void registrarQueso(GridPane form) {
        try {
            ComboBox<String> tipoCombo = (ComboBox<String>) form.getProperties().get("tipoCombo");
            TextField litrosField = (TextField) form.getProperties().get("litrosField");
            TextField pesoField = (TextField) form.getProperties().get("pesoField");
            
            Queso queso = new Queso();
            queso.setTipo(tipoCombo.getValue());
            queso.setLitrosLeche(Double.parseDouble(litrosField.getText()));
            queso.setPesoKg(Double.parseDouble(pesoField.getText()));
            queso.setFechaElaboracion(LocalDate.now());
            
            produccionService.registrarQueso(queso);
            
            // Mostrar confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("Queso registrado con ID: " + queso.getId());
            alert.showAndWait();
            
        } catch (NumberFormatException e) {
            showError("Error", "Ingrese valores numéricos válidos.");
        } catch (Exception e) {
            showError("Error", "No se pudo registrar el queso: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
