package ar.gob.lapampa.queserianc.service;

import ar.gob.lapampa.queserianc.model.Queso;
import ar.gob.lapampa.queserianc.storage.Database;

import java.util.List;
import java.util.UUID;

public class ProduccionService {
    private final Database db;
    
    public ProduccionService(Database db) {
        this.db = db;
    }
    
    // Registrar un nuevo queso
    public void registrarQueso(Queso queso) {
        if (queso.getId() == null || queso.getId().isEmpty()) {
            queso.setId(UUID.randomUUID().toString());
        }
        
        // Validaciones básicas
        if (queso.getTipo() == null || queso.getTipo().isEmpty()) {
            throw new IllegalArgumentException("El tipo de queso es obligatorio");
        }
        if (queso.getLitrosLeche() <= 0) {
            throw new IllegalArgumentException("Los litros de leche deben ser mayores a 0");
        }
        if (queso.getPesoKg() <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0");
        }
        if (queso.getFechaElaboracion() == null) {
            queso.setFechaElaboracion(java.time.LocalDate.now());
        }
        
        db.guardarQueso(queso);
    }
    
    // Obtener todos los quesos
    public List<Queso> obtenerTodos() {
        return db.obtenerTodosQuesos();
    }
    
    // Obtener quesos por fecha
    public List<Queso> obtenerPorFecha(java.time.LocalDate fecha) {
        return db.obtenerQuesosPorFecha(fecha);
    }
    
    // Obtener quesos por tipo
    public List<Queso> obtenerPorTipo(String tipo) {
        return db.obtenerQuesosPorTipo(tipo);
    }
    
    // Actualizar queso existente
    public void actualizarQueso(Queso queso) {
        if (queso.getId() == null || queso.getId().isEmpty()) {
            throw new IllegalArgumentException("ID del queso es obligatorio para actualizar");
        }
        db.actualizarQueso(queso);
    }
    
    // Eliminar queso
    public void eliminarQueso(String id) {
        db.eliminarQueso(id);
    }
}
