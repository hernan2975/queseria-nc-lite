package ar.gob.lapampa.queserianc.model;

import java.time.LocalDate;
import java.util.UUID;

public class MateriaPrima {
    private String id;
    private String tipo; // "leche", "cultivo", "cuajo", "sal"
    private double cantidad;
    private String unidad; // "litros", "gramos", "mililitros"
    private String proveedor;
    private String loteProveedor;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private boolean aptoConsumo;
    
    public MateriaPrima() {
        this.id = UUID.randomUUID().toString();
        this.fechaIngreso = LocalDate.now();
        this.aptoConsumo = true;
    }
    
    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    
    public String getLoteProveedor() { return loteProveedor; }
    public void setLoteProveedor(String loteProveedor) { this.loteProveedor = loteProveedor; }
    
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public boolean isAptoConsumo() { return aptoConsumo; }
    public void setAptoConsumo(boolean aptoConsumo) { this.aptoConsumo = aptoConsumo; }
    
    // Método para verificar si está vencido
    public boolean isVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }
}
