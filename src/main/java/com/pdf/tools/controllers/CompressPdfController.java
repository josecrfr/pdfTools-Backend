package com.pdf.tools.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.tools.services.PdfCompresService;

@RestController
@RequestMapping("/api/pdf")
public class CompressPdfController {

    private final PdfCompresService service;

    public CompressPdfController(PdfCompresService service) {
        this.service = service;
    }

    @PostMapping("/compress")
    public ResponseEntity<byte[]> compress(
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetKb") Integer targetKb) throws Exception {

        byte[] result = service.compress(file, targetKb);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=compressed.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(result);
    }
}
