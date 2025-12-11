package ar.gob.lapampa.queserianc.model;

import java.time.LocalDate;
import java.util.List;

public class Queso {
    private String id;
    private String tipo; // "criollo", "sardo", "provolone", "quesillo"
    private double litrosLeche;
    private double pesoKg;
    private LocalDate fechaElaboracion;
    private int diasMaduracion;
    private String loteSenasa;
    private List<ControlCalidad> controles;
    
    // Constructores, getters y setters
    public Queso() {}
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public double getLitrosLeche() { return litrosLeche; }
    public void setLitrosLeche(double litrosLeche) { this.litrosLeche = litrosLeche; }
    
    public String getLoteSenasa() { return loteSenasa; }
    public void setLoteSenasa(String loteSenasa) { this.loteSenasa = loteSenasa; }
    
    public LocalDate getFechaElaboracion() { return fechaElaboracion; }
    public void setFechaElaboracion(LocalDate fechaElaboracion) { 
        this.fechaElaboracion = fechaElaboracion; 
    }
    
    // Calcular fecha de vencimiento (60 días para frescos, 180 para madurados)
    public LocalDate getFechaVencimiento() {
        int dias = "quesillo".equals(tipo) ? 60 : 180;
        return fechaElaboracion.plusDays(dias);
    }
}
