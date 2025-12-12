package ar.gob.lapampa.queserianc;

import ar.gob.lapampa.queserianc.storage.Database;
import ar.gob.lapampa.queserianc.storage.DataLoader;
import ar.gob.lapampa.queserianc.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QueseriaNcApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializar base de datos
            Database db = new Database();
            DataLoader loader = new DataLoader(db);
            loader.cargarDatosIniciales();
            
            // Crear vista principal
            MainView mainView = new MainView();
            
            // Configurar ventana
            Scene scene = new Scene(mainView, 1024, 768);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            primaryStage.setTitle("Quesería NC Lite - Jacinto Arauz");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
