package ar.gob.lapampa.queserianc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Lote {
    private String codigo; // QN-LP-20250615-001
    private LocalDate fechaElaboracion;
    private String tipoQueso;
    private int cantidadUnidades;
    private double pesoTotalKg;
    private String rnpa; // RNPA del establecimiento
    private List<String> idsQuesos;
    
    public Lote() {
        this.idsQuesos = new ArrayList<>();
        this.rnpa = "12-3456789-0"; // RNPA de Quesería NC
    }
    
    // Método para generar código SENASA
    public static String generarCodigoSenasa(LocalDate fecha, int numero) {
        return String.format("QN-LP-%s-%03d", 
            fecha.toString().replace("-", ""), 
            numero);
    }
    
    // Getters y setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getRnpa() { return rnpa; }
}
