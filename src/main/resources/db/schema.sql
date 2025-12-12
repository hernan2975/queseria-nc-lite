-- Base de datos para Quesería NC
CREATE TABLE IF NOT EXISTS quesos (
    id TEXT PRIMARY KEY,
    tipo TEXT NOT NULL,
    litros_leche REAL NOT NULL,
    peso_kg REAL NOT NULL,
    fecha_elaboracion DATE NOT NULL,
    dias_maduracion INTEGER DEFAULT 0,
    lote_senasa TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lotes (
    codigo TEXT PRIMARY KEY,
    fecha_elaboracion DATE NOT NULL,
    tipo_queso TEXT NOT NULL,
    cantidad_unidades INTEGER NOT NULL,
    peso_total_kg REAL NOT NULL,
    rnpa TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS controles_calidad (
    id TEXT PRIMARY KEY,
    queso_id TEXT NOT NULL,
    ph REAL,
    acidez REAL,
    temperatura REAL,
    humedad REAL,
    fecha_control DATE NOT NULL,
    FOREIGN KEY (queso_id) REFERENCES quesos(id)
);

-- Índices para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_quesos_lote ON quesos(lote_senasa);
CREATE INDEX IF NOT EXISTS idx_quesos_fecha ON quesos(fecha_elaboracion);
CREATE INDEX IF NOT EXISTS idx_lotes_fecha ON lotes(fecha_elaboracion);
