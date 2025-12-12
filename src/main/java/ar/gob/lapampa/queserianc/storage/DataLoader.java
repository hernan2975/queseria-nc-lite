package ar.gob.lapampa.queserianc.storage;

import ar.gob.lapampa.queserianc.model.MateriaPrima;

import java.time.LocalDate;

public class DataLoader {
    private final Database db;
    
    public DataLoader(Database db) {
        this.db = db;
    }
    
    public void cargarDatosIniciales() {
        // Verificar si ya hay datos
        if (!hayDatos()) {
            cargarMateriasPrimas();
            System.out.println("✅ Datos iniciales cargados");
        }
    }
    
    private boolean hayDatos() {
        // Verificar si hay quesos registrados
        try {
            return !db.obtenerTodosQuesos().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    private void cargarMateriasPrimas() {
        // Materias primas típicas para quesería artesanal
        MateriaPrima leche = new MateriaPrima();
        leche.setTipo("leche");
        leche.setCantidad(100.0);
        leche.setUnidad("litros");
        leche.setProveedor("Tambo Local");
        leche.setLoteProveedor("L-20250615");
        leche.setFechaIngreso(LocalDate.now());
        leche.setFechaVencimiento(LocalDate.now().plusDays(2));
        db.guardarMateriaPrima(leche);
        
        MateriaPrima cultivo = new MateriaPrima();
        cultivo.setTipo("cultivo");
        cultivo.setCantidad(50.0);
        cultivo.setUnidad("gramos");
        cultivo.setProveedor("Laboratorio Lácteo");
        cultivo.setLoteProveedor("C-20250601");
        cultivo.setFechaIngreso(LocalDate.now());
        cultivo.setFechaVencimiento(LocalDate.now().plusMonths(6));
        db.guardarMateriaPrima(cultivo);
        
        MateriaPrima cuajo = new MateriaPrima();
        cuajo.setTipo("cuajo");
        cuajo.setCantidad(100.0);
        cuajo.setUnidad("mililitros");
        cuajo.setProveedor("Distribuidora Láctea");
        cuajo.setLoteProveedor("CU-20250520");
        cuajo.setFechaIngreso(LocalDate.now());
        cuajo.setFechaVencimiento(LocalDate.now().plusMonths(12));
        db.guardarMateriaPrima(cuajo);
        
        MateriaPrima sal = new MateriaPrima();
        sal.setTipo("sal");
        sal.setCantidad(5.0);
        sal.setUnidad("kilogramos");
        sal.setProveedor("Salinas Pagrún");
        sal.setLoteProveedor("S-20250115");
        sal.setFechaIngreso(LocalDate.now());
        sal.setFechaVencimiento(LocalDate.now().plusYears(2));
        db.guardarMateriaPrima(sal);
    }
}
