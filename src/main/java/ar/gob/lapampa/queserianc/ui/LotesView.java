package ar.gob.lapampa.queserianc.ui;

import ar.gob.lapampa.queserianc.model.Lote;
import ar.gob.lapampa.queserianc.model.Queso;
import ar.gob.lapampa.queserianc.service.TrazabilidadService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class LotesView extends VBox {
    private final TrazabilidadService trazabilidadService;
    private final TableView<Queso> quesoTable;
    private final ObservableList<Queso> quesoData;
    private final TextField loteField;
    
    public LotesView(TrazabilidadService trazabilidadService) {
        this.trazabilidadService = trazabilidadService;
        this.quesoData = FXCollections.observableArrayList();
        
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        
        Label title = new Label("Gestión de Lotes SENASA");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Tabla de quesos sin lote
        quesoTable = crearTablaQuesos();
        cargarQuesosSinLote();
        
        // Formulario de lote
        GridPane form = crearFormularioLote();
        loteField = (TextField) form.getChildren().get(3); // Referencia al campo de lote
        
        // Botones
        HBox buttons = new HBox(10);
        Button generarBtn = new Button("Generar Lote SENASA");
        generarBtn.setOnAction(e -> generarLote());
        
        Button buscarBtn = new Button("Buscar Lote");
        buscarBtn.setOnAction(e -> buscarLote());
        
        buttons.getChildren().addAll(generarBtn, buscarBtn);
        
        this.getChildren().addAll(title, new Label("Quesos sin asignar a lote:"), quesoTable, 
                                 new Label("Generar nuevo lote:"), form, buttons);
    }
    
    private TableView<Queso> crearTablaQuesos() {
        TableView<Queso> table = new TableView<>();
        table.setPrefHeight(200);
        
        TableColumn<Queso, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cell -> cell.getValue().tipoProperty());
        
        TableColumn<Queso, Double> litrosCol = new TableColumn<>("Litros Leche");
        litrosCol.setCellValueFactory(cell -> cell.getValue().litrosLecheProperty().asObject());
        
        TableColumn<Queso, Double> pesoCol = new TableColumn<>("Peso (kg)");
        pesoCol.setCellValueFactory(cell -> cell.getValue().pesoKgProperty().asObject());
        
        TableColumn<Queso, String> fechaCol = new TableColumn<>("Fecha");
        fechaCol.setCellValueFactory(cell -> 
            cell.getValue().fechaElaboracionProperty().asString("dd/MM/yyyy"));
        
        table.getColumns().addAll(tipoCol, litrosCol, pesoCol, fechaCol);
        table.setItems(quesoData);
        
        return table;
    }
    
    private GridPane crearFormularioLote() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.add(new Label("Tipo de queso:"), 0, 0);
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("criollo", "sardo", "provolone", "quesillo");
        tipoCombo.setValue("criollo");
        grid.add(tipoCombo, 1, 0);
        
        grid.add(new Label("Cantidad unidades:"), 0, 1);
        TextField cantidadField = new TextField("10");
        grid.add(cantidadField, 1, 1);
        
        grid.add(new Label("Lote generado:"), 0, 2);
        TextField loteField = new TextField();
        loteField.setEditable(false);
        grid.add(loteField, 1, 2);
        
        // Guardar referencias
        grid.getProperties().put("tipoCombo", tipoCombo);
        grid.getProperties().put("cantidadField", cantidadField);
        grid.getProperties().put("loteField", loteField);
        
        return grid;
    }
    
    private void cargarQuesosSinLote() {
        // En una implementación real, se cargarían de la BD
        // Aquí usamos datos de ejemplo
        quesoData.clear();
        quesoData.add(new Queso());
        quesoData.add(new Queso());
    }
    
    private void generarLote() {
        try {
            GridPane form = (GridPane) this.getChildren().get(5);
            ComboBox<String> tipoCombo = (ComboBox<String>) form.getProperties().get("tipoCombo");
            TextField cantidadField = (TextField) form.getProperties().get("cantidadField");
            TextField loteField = (TextField) form.getProperties().get("loteField");
            
            String tipo = tipoCombo.getValue();
            int cantidad = Integer.parseInt(cantidadField.getText());
            
            // Generar código de lote
            String codigo = Lote.generarCodigoSenasa(LocalDate.now(), 1);
            loteField.setText(codigo);
            
            // Mostrar confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lote Generado");
            alert.setHeaderText(null);
            alert.setContentText("Lote SENASA generado: " + codigo + "\n\n" +
                               "• Tipo: " + tipo + "\n" +
                               "• Cantidad: " + cantidad + " unidades\n" +
                               "• RNPA: 12-3456789-0");
            alert.showAndWait();
            
        } catch (NumberFormatException e) {
            showError("Error", "Ingrese una cantidad válida.");
        } catch (Exception e) {
            showError("Error", "No se pudo generar el lote: " + e.getMessage());
        }
    }
    
    private void buscarLote() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Lote");
        dialog.setHeaderText("Ingrese código de lote SENASA");
        dialog.setContentText("Código:");
        
        dialog.showAndWait().ifPresent(codigo -> {
            if (!codigo.trim().isEmpty()) {
                Lote lote = trazabilidadService.buscarPorCodigo(codigo.trim());
                if (lote != null) {
                    mostrarDetalleLote(lote);
                } else {
                    showError("No encontrado", "Lote no encontrado: " + codigo);
                }
            }
        });
    }
    
    private void mostrarDetalleLote(Lote lote) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lote: " + lote.getCodigo());
        alert.setHeaderText("Detalles del lote");
        alert.setContentText(
            "Tipo de queso: " + lote.getTipoQueso() + "\n" +
            "Fecha elaboración: " + lote.getFechaElaboracion() + "\n" +
            "Cantidad unidades: " + lote.getCantidadUnidades() + "\n" +
            "Peso total: " + String.format("%.2f kg", lote.getPesoTotalKg()) + "\n" +
            "RNPA: " + lote.getRnpa() + "\n\n" +
            "✅ Listo para etiquetar y distribuir"
        );
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
