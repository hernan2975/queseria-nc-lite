package ar.gob.lapampa.queserianc.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ControlCalidad {
    private String id;
    private String quesoId;
    private double ph;
    private double acidez;
    private double temperatura;
    private double humedad;
    private String observaciones;
    private LocalDateTime fechaControl;
    private String operario;
    
    public ControlCalidad() {
        this.id = UUID.randomUUID().toString();
        this.fechaControl = LocalDateTime.now();
    }
    
    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getQuesoId() { return quesoId; }
    public void setQuesoId(String quesoId) { this.quesoId = quesoId; }
    
    public double getPh() { return ph; }
    public void setPh(double ph) { this.ph = ph; }
    
    public double getAcidez() { return acidez; }
    public void setAcidez(double acidez) { this.acidez = acidez; }
    
    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }
    
    public double getHumedad() { return humedad; }
    public void setHumedad(double humedad) { this.humedad = humedad; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public LocalDateTime getFechaControl() { return fechaControl; }
    public void setFechaControl(LocalDateTime fechaControl) { this.fechaControl = fechaControl; }
    
    public String getOperario() { return operario; }
    public void setOperario(String operario) { this.operario = operario; }
    
    // Validar según normativa SENASA para quesos artesanales
    public boolean esApto() {
        // Valores referenciales para queso fresco (ajustables por tipo)
        boolean phOk = ph >= 5.0 && ph <= 5.5;
        boolean acidezOk = acidez >= 0.18 && acidez <= 0.22;
        boolean tempOk = temperatura >= 4.0 && temperatura <= 10.0;
        return phOk && acidezOk && tempOk;
    }
}
