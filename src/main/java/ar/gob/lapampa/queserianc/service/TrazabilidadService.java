package ar.gob.lapampa.queserianc.service;

import ar.gob.lapampa.queserianc.model.Lote;
import ar.gob.lapampa.queserianc.model.Queso;
import ar.gob.lapampa.queserianc.storage.Database;

import java.time.LocalDate;
import java.util.List;

public class TrazabilidadService {
    private final Database db;
    
    public TrazabilidadService(Database db) {
        this.db = db;
    }
    
    // Generar lote SENASA para un grupo de quesos
    public Lote generarLote(List<Queso> quesos) {
        // Obtener siguiente número de lote para la fecha
        LocalDate hoy = LocalDate.now();
        int numero = db.obtenerSiguienteNumeroLote(hoy) + 1;
        
        Lote lote = new Lote();
        lote.setCodigo(Lote.generarCodigoSenasa(hoy, numero));
        lote.setFechaElaboracion(hoy);
        lote.setTipoQueso(quesos.get(0).getTipo());
        lote.setCantidadUnidades(quesos.size());
        lote.setPesoTotalKg(quesos.stream().mapToDouble(Queso::getPesoKg).sum());
        
        // Asociar quesos al lote
        for (Queso queso : quesos) {
            queso.setLoteSenasa(lote.getCodigo());
            lote.getIdsQuesos().add(queso.getId());
            db.actualizarQueso(queso);
        }
        
        db.guardarLote(lote);
        return lote;
    }
    
    // Buscar lote por código
    public Lote buscarPorCodigo(String codigo) {
        return db.obtenerLotePorCodigo(codigo);
    }
}
