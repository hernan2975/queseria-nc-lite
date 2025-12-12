-- Base de datos para Quesería NC - Jacinto Arauz
-- Compatible con SQLite 3.35+

-- Tabla de quesos
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

-- Tabla de lotes SENASA
CREATE TABLE IF NOT EXISTS lotes (
    codigo TEXT PRIMARY KEY,
    fecha_elaboracion DATE NOT NULL,
    tipo_queso TEXT NOT NULL,
    cantidad_unidades INTEGER NOT NULL,
    peso_total_kg REAL NOT NULL,
    rnpa TEXT NOT NULL DEFAULT '12-3456789-0',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de controles de calidad
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
    FOREIGN KEY (queso_id) REFERENCES quesos(id) ON DELETE CASCADE
);

-- Tabla de materias primas
CREATE TABLE IF NOT EXISTS materias_primas (
    id TEXT PRIMARY KEY,
    tipo TEXT NOT NULL,          -- leche, cultivo, cuajo, sal
    cantidad REAL NOT NULL,
    unidad TEXT NOT NULL,        -- litros, gramos, mililitros
    proveedor TEXT,
    lote_proveedor TEXT,
    fecha_ingreso DATE NOT NULL,
    fecha_vencimiento DATE,
    apto_consumo BOOLEAN DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimización
CREATE INDEX IF NOT EXISTS idx_quesos_lote ON quesos(lote_senasa);
CREATE INDEX IF NOT EXISTS idx_quesos_fecha ON quesos(fecha_elaboracion);
CREATE INDEX IF NOT EXISTS idx_quesos_tipo ON quesos(tipo);
CREATE INDEX IF NOT EXISTS idx_lotes_fecha ON lotes(fecha_elaboracion);
CREATE INDEX IF NOT EXISTS idx_calidad_queso ON controles_calidad(queso_id);
CREATE INDEX IF NOT EXISTS idx_materias_tipo ON materias_primas(tipo);
CREATE INDEX IF NOT EXISTS idx_materias_vencimiento ON materias_primas(fecha_vencimiento);

-- Datos iniciales (RNPA de ejemplo)
INSERT OR IGNORE INTO lotes (codigo, fecha_elaboracion, tipo_queso, cantidad_unidades, peso_total_kg, rnpa)
VALUES ('QN-LP-20250101-000', '2025-01-01', 'criollo', 1, 0.0, '12-3456789-0');
