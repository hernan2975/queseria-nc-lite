package ar.gob.lapampa.queserianc.storage;

import ar.gob.lapampa.queserianc.model.ControlCalidad;
import ar.gob.lapampa.queserianc.model.Lote;
import ar.gob.lapampa.queserianc.model.MateriaPrima;
import ar.gob.lapampa.queserianc.model.Queso;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Database {
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static final String DB_URL = "jdbc:sqlite:data/queseria-nc.db";
    
    public Database() {
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            
            // Crear tablas si no existen
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS quesos (
                    id TEXT PRIMARY KEY,
                    tipo TEXT NOT NULL,
                    litros_leche REAL NOT NULL,
                    peso_kg REAL NOT NULL,
                    fecha_elaboracion DATE NOT NULL,
                    dias_maduracion INTEGER DEFAULT 0,
                    lote_senasa TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS lotes (
                    codigo TEXT PRIMARY KEY,
                    fecha_elaboracion DATE NOT NULL,
                    tipo_queso TEXT NOT NULL,
                    cantidad_unidades INTEGER NOT NULL,
                    peso_total_kg REAL NOT NULL,
                    rnpa TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS controles_calidad (
                    id TEXT PRIMARY KEY,
                    queso_id TEXT NOT NULL,
                    ph REAL,
                    acidez REAL,
                    temperatura REAL,
                    humedad REAL,
                    observaciones TEXT,
                    operario TEXT,
                    fecha_control TIMESTAMP NOT NULL,
                    FOREIGN KEY (queso_id) REFERENCES quesos(id)
                )
                """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS materias_primas (
                    id TEXT PRIMARY KEY,
                    tipo TEXT NOT NULL,
                    cantidad REAL NOT NULL,
                    unidad TEXT NOT NULL,
                    proveedor TEXT,
                    lote_proveedor TEXT,
                    fecha_ingreso DATE NOT NULL,
                    fecha_vencimiento DATE,
                    apto_consumo BOOLEAN DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            
            // Índices
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_quesos_lote ON quesos(lote_senasa)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_quesos_fecha ON quesos(fecha_elaboracion)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_lotes_fecha ON lotes(fecha_elaboracion)");
            
            LOGGER.info("Base de datos inicializada correctamente");
            
        } catch (SQLException e) {
            LOGGER.severe("Error al inicializar la base de datos: " + e.getMessage());
            throw new RuntimeException("No se pudo inicializar la base de datos", e);
        }
    }
    
    // === OPERACIONES CON QUESOS ===
    public void guardarQueso(Queso queso) {
        String sql = "INSERT OR REPLACE INTO quesos (id, tipo, litros_leche, peso_kg, fecha_elaboracion, dias_maduracion, lote_senasa) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, queso.getId());
            pstmt.setString(2, queso.getTipo());
            pstmt.setDouble(3, queso.getLitrosLeche());
            pstmt.setDouble(4, java.util.Optional.ofNullable(queso.getPesoKg()).orElse(0.0));
            pstmt.setDate(5, java.sql.Date.valueOf(queso.getFechaElaboracion()));
            pstmt.setInt(6, queso.getDiasMaduracion());
            pstmt.setString(7, queso.getLoteSenasa());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar queso", e);
        }
    }
    
    public void actualizarQueso(Queso queso) {
        guardarQueso(queso); // INSERT OR REPLACE actúa como update si existe
    }
    
    public void eliminarQueso(String id) {
        String sql = "DELETE FROM quesos WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar queso", e);
        }
    }
    
    public List<Queso> obtenerTodosQuesos() {
        return obtenerQuesos("SELECT * FROM quesos ORDER BY fecha_elaboracion DESC, created_at DESC");
    }
    
    public List<Queso> obtenerQuesosPorFecha(LocalDate fecha) {
        String sql = "SELECT * FROM quesos WHERE fecha_elaboracion = ?";
        return obtenerQuesos(sql, java.sql.Date.valueOf(fecha));
    }
    
    public List<Queso> obtenerQuesosPorTipo(String tipo) {
        String sql = "SELECT * FROM quesos WHERE tipo = ?";
        return obtenerQuesos(sql, tipo);
    }
    
    private List<Queso> obtenerQuesos(String sql, Object... params) {
        List<Queso> quesos = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Queso q = new Queso();
                q.setId(rs.getString("id"));
                q.setTipo(rs.getString("tipo"));
                q.setLitrosLeche(rs.getDouble("litros_leche"));
                q.setPesoKg(rs.getDouble("peso_kg"));
                q.setFechaElaboracion(rs.getDate("fecha_elaboracion").toLocalDate());
                q.setDiasMaduracion(rs.getInt("dias_maduracion"));
                q.setLoteSenasa(rs.getString("lote_senasa"));
                quesos.add(q);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener quesos", e);
        }
        
        return quesos;
    }
    
    // === OPERACIONES CON LOTES ===
    public void guardarLote(Lote lote) {
        String sql = "INSERT OR REPLACE INTO lotes (codigo, fecha_elaboracion, tipo_queso, cantidad_unidades, peso_total_kg, rnpa) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lote.getCodigo());
            pstmt.setDate(2, java.sql.Date.valueOf(lote.getFechaElaboracion()));
            pstmt.setString(3, lote.getTipoQueso());
            pstmt.setInt(4, lote.getCantidadUnidades());
            pstmt.setDouble(5, lote.getPesoTotalKg());
            pstmt.setString(6, lote.getRnpa());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar lote", e);
        }
    }
    
    public Lote obtenerLotePorCodigo(String codigo) {
        String sql = "SELECT * FROM lotes WHERE codigo = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Lote lote = new Lote();
                lote.setCodigo(rs.getString("codigo"));
                lote.setFechaElaboracion(rs.getDate("fecha_elaboracion").toLocalDate());
                lote.setTipoQueso(rs.getString("tipo_queso"));
                lote.setCantidadUnidades(rs.getInt("cantidad_unidades"));
                lote.setPesoTotalKg(rs.getDouble("peso_total_kg"));
                lote.setRnpa(rs.getString("rnpa"));
                return lote;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener lote", e);
        }
        
        return null;
    }
    
    public int obtenerSiguienteNumeroLote(LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM lotes WHERE fecha_elaboracion = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.warning("Error al obtener número de lote: " + e.getMessage());
        }
        
        return 0;
    }
    
    // === OPERACIONES CON CONTROL DE CALIDAD ===
    public void guardarControlCalidad(ControlCalidad control) {
        String sql = """
            INSERT OR REPLACE INTO controles_calidad 
            (id, queso_id, ph, acidez, temperatura, humedad, observaciones, operario, fecha_control) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, control.getId());
            pstmt.setString(2, control.getQuesoId());
            pstmt.setDouble(3, control.getPh());
            pstmt.setDouble(4, control.getAcidez());
            pstmt.setDouble(5, control.getTemperatura());
            pstmt.setDouble(6, control.getHumedad());
            pstmt.setString(7, control.getObservaciones());
            pstmt.setString(8, control.getOperario());
            pstmt.setTimestamp(9, java.sql.Timestamp.valueOf(control.getFechaControl()));
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar control de calidad", e);
        }
    }
    
    // === OPERACIONES CON MATERIA PRIMA ===
    public void guardarMateriaPrima(MateriaPrima materia) {
        String sql = """
            INSERT OR REPLACE INTO materias_primas 
            (id, tipo, cantidad, unidad, proveedor, lote_proveedor, fecha_ingreso, fecha_vencimiento, apto_consumo) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, materia.getId());
            pstmt.setString(2, materia.getTipo());
            pstmt.setDouble(3, materia.getCantidad());
            pstmt.setString(4, materia.getUnidad());
            pstmt.setString(5, materia.getProveedor());
            pstmt.setString(6, materia.getLoteProveedor());
            pstmt.setDate(7, java.sql.Date.valueOf(materia.getFechaIngreso()));
            pstmt.setDate(8, materia.getFechaVencimiento() != null ? 
                java.sql.Date.valueOf(materia.getFechaVencimiento()) : null);
            pstmt.setBoolean(9, materia.isAptoConsumo());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar materia prima", e);
        }
    }
}
