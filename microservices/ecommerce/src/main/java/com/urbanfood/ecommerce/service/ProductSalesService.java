package com.urbanfood.ecommerce.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.urbanfood.ecommerce.entity.BestSellingProductDTO;
import com.urbanfood.ecommerce.repository.ProductSalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Date;

@Service
public class ProductSalesService {

    @Autowired
    private ProductSalesRepository salesReportRepository;

    public byte[] generateSalesReportPdf() {
        List<BestSellingProductDTO> bestSellingProducts = salesReportRepository.fetchBestSellingProducts();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font tableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font tableBody = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Best Selling Products Report", titleFont));
            document.add(new Paragraph("Generated on: " + new Date().toString()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 2});

            // Header
            PdfPCell h1 = new PdfPCell(new Phrase("Product Name", tableHeader));
            PdfPCell h2 = new PdfPCell(new Phrase("Total Quantity Sold", tableHeader));
            table.addCell(h1);
            table.addCell(h2);

            // Data
            for (BestSellingProductDTO product : bestSellingProducts) {
                table.addCell(new Phrase(product.getProductName(), tableBody));
                table.addCell(new Phrase(String.valueOf(product.getTotalSold()), tableBody));
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
