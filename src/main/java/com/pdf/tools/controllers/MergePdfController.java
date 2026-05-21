package com.pdf.tools.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.tools.services.PdfMergeService;

@RestController
@RequestMapping("/api/pdf")
public class MergePdfController {

    private final PdfMergeService pdfMergeService;

    public MergePdfController(PdfMergeService pdfMergeService) {
        this.pdfMergeService = pdfMergeService;
    }

    @PostMapping("/merge")
    public ResponseEntity<byte[]> merge(@RequestParam("files") MultipartFile[] files) throws Exception {

        byte[] result = pdfMergeService.merge(files);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merged.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(result);
    }
}