package com.pdf.tools.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.tools.services.PdfSplitService;

@RestController
@RequestMapping("/api/pdf")
public class PdfSplitController {
    private final PdfSplitService pdfService;

    public PdfSplitController(PdfSplitService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/split")
    public ResponseEntity<byte[]> split(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mode") String mode,
            @RequestParam(value = "range", required = false) String range) throws Exception {

        byte[] result = pdfService.splitPdf(file, mode, range);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=result.zip")
                .body(result);
    }

}
