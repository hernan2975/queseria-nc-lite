package ar.gob.lapampa.queserianc.service;

import ar.gob.lapampa.queserianc.model.Lote;
import ar.gob.lapampa.queserianc.model.Queso;
import ar.gob.lapampa.queserianc.storage.Database;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteService {
    private final Database db;
    
    public ReporteService(Database db) {
        this.db = db;
    }
    
    // Generar reporte diario de producción (PDF)
    public void generarReporteDiario(String rutaArchivo, LocalDate fecha) throws IOException {
        try (PdfWriter writer = new PdfWriter(rutaArchivo);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Título
            document.add(new Paragraph("Quesería NC - Jacinto Arauz")
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Reporte Diario de Producción")
                    .setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            // Tabla de producción
            Table table = new Table(5);
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Litros Leche").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Peso (kg)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Lote SENASA").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Vencimiento").setBold()));
            
            List<Queso> quesos = db.obtenerQuesosPorFecha(fecha);
            double totalLitros = 0;
            double totalPeso = 0;
            
            for (Queso q : quesos) {
                table.addCell(q.getTipo());
                table.addCell(String.format("%.1f", q.getLitrosLeche()));
                table.addCell(String.format("%.2f", q.getPesoKg()));
                table.addCell(q.getLoteSenasa() != null ? q.getLoteSenasa() : "—");
                table.addCell(q.getFechaVencimiento() != null ? 
                    q.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—");
                
                totalLitros += q.getLitrosLeche();
                totalPeso += q.getPesoKg();
            }
            
            // Fila de totales
            table.addCell(new Cell(1, 2).add(new Paragraph("TOTAL").setBold()));
            table.addCell(new Paragraph(String.format("%.1f", totalLitros)).setBold());
            table.addCell(new Paragraph(String.format("%.2f", totalPeso)).setBold());
            table.addCell("");
            
            document.add(table);
            
            // Notas
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Este reporte cumple con los requisitos de trazabilidad de la SENASA Res. 433/2023.")
                    .setFontSize(10).setItalic());
            document.add(new Paragraph("RNPA Establecimiento: 12-3456789-0 | Establecimiento Nº: 001-LP")
                    .setFontSize(10));
        }
    }
    
    // Generar etiqueta para lote SENASA
    public void generarEtiquetaLote(String rutaArchivo, String codigoLote) throws IOException {
        Lote lote = db.obtenerLotePorCodigo(codigoLote);
        if (lote == null) {
            throw new IllegalArgumentException("Lote no encontrado: " + codigoLote);
        }
        
        try (PdfWriter writer = new PdfWriter(rutaArchivo);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Configurar página pequeña (etiqueta 10x5 cm)
            pdf.setDefaultPageSize(283, 142); // 10x5 cm en puntos
            
            document.add(new Paragraph("QUESERÍA NC").setFontSize(16).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Jacinto Arauz, La Pampa").setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("LOTE SENASA").setFontSize(12).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(codigoLote).setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Tipo: " + lote.getTipoQueso()).setFontSize(10));
            document.add(new Paragraph("Fecha elaboración: " + 
                lote.getFechaElaboracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFontSize(10));
            document.add(new Paragraph("Peso total: " + String.format("%.2f kg", lote.getPesoTotalKg())).setFontSize(10));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("RNPA: " + lote.getRnpa()).setFontSize(9));
            document.add(new Paragraph("Elaborado en establecimiento inscripto").setFontSize(8).setItalic());
        }
    }
}
