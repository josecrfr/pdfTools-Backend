package com.pdf.tools.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfMergeService {

    public byte[] merge(MultipartFile[] files) throws Exception {

        if (files == null) {
            throw new IllegalArgumentException("No se han enviado archivos");
        }

        if (files.length < 2) {
            throw new IllegalArgumentException("Debes subir al menos 2 PDFs para unirlos");
        }

        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        merger.setDestinationStream(outputStream);

        boolean hasValidFile = false;

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
                throw new IllegalArgumentException(
                        "Archivo inválido: " + file.getOriginalFilename() + " (solo PDFs permitidos)");
            }

            InputStream inputStream = file.getInputStream();

            merger.addSource(inputStream);

            hasValidFile = true;
        }

        if (!hasValidFile) {
            throw new IllegalArgumentException("No hay archivos PDF válidos para unir");
        }

        merger.mergeDocuments(null);

        return outputStream.toByteArray();
    }
}