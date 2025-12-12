package ar.gob.lapampa.queserianc.ui;

import ar.gob.lapampa.queserianc.service.ProduccionService;
import ar.gob.lapampa.queserianc.service.TrazabilidadService;
import ar.gob.lapampa.queserianc.service.ReporteService;
import ar.gob.lapampa.queserianc.storage.Database;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {
    
    public MainView() {
        Database db = new Database();
        ProduccionService produccionService = new ProduccionService(db);
        TrazabilidadService trazabilidadService = new TrazabilidadService(db);
        ReporteService reporteService = new ReporteService(db);
        
        // Crear pestañas
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab produccionTab = new Tab(".Producción");
        produccionTab.setContent(new ProduccionView(produccionService));
        
        Tab lotesTab = new Tab("Lotes SENASA");
        lotesTab.setContent(new LotesView(trazabilidadService));
        
        Tab reportesTab = new Tab("Reportes");
        reportesTab.setContent(new ReportesView(reporteService));
        
        tabPane.getTabs().addAll(produccionTab, lotesTab, reportesTab);
        
        // Estilo
        tabPane.setStyle("-fx-font-size: 14px;");
        
        this.setCenter(tabPane);
        this.setPadding(new Insets(10));
    }
}
