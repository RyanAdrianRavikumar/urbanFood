package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.service.ProductSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductSalesController {

    @Autowired
    private ProductSalesService salesReportService;

    @GetMapping("/reports/sales")
    public ResponseEntity<byte[]> generateSalesReport() {
        byte[] pdfBytes = salesReportService.generateSalesReportPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment").filename("sales_report.pdf").build()
        );

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
