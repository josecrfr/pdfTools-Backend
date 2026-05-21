package com.pdf.tools.services;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfSplitService {

    public byte[] splitPdf(MultipartFile file, String mode, String range) throws Exception {

        PDDocument doc = PDDocument.load(file.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(baos);

        try {

            if ("EVERY_PAGE".equals(mode)) {

                for (int i = 0; i < doc.getNumberOfPages(); i++) {

                    PDDocument newDoc = new PDDocument();
                    newDoc.addPage(doc.getPage(i));

                    ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
                    newDoc.save(pdfOut);
                    newDoc.close();

                    zip.putNextEntry(new ZipEntry("page-" + (i + 1) + ".pdf"));
                    zip.write(pdfOut.toByteArray());
                    zip.closeEntry();
                }

            } else if ("PAGES".equals(mode)) {

                List<Integer> pages = parsePages(range, doc.getNumberOfPages());

                PDDocument out = new PDDocument();

                for (Integer p : pages) {
                    validatePage(p, doc.getNumberOfPages());
                    out.addPage(doc.getPage(p - 1));
                }

                ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
                out.save(pdfOut);
                out.close();

                zip.putNextEntry(new ZipEntry("extract.pdf"));
                zip.write(pdfOut.toByteArray());
                zip.closeEntry();

            } else if ("RANGE".equals(mode)) {

                List<Integer> pages = parsePages(range, doc.getNumberOfPages());

                PDDocument out = new PDDocument();

                for (Integer p : pages) {
                    validatePage(p, doc.getNumberOfPages());
                    out.addPage(doc.getPage(p - 1));
                }

                ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
                out.save(pdfOut);
                out.close();

                zip.putNextEntry(new ZipEntry("range.pdf"));
                zip.write(pdfOut.toByteArray());
                zip.closeEntry();
            }

        } finally {
            doc.close();
            zip.close();
        }

        return baos.toByteArray();
    }

    private List<Integer> parsePages(String range, int maxPages) {

        List<Integer> pages = new ArrayList<>();

        if (range == null || range.isBlank()) {
            throw new IllegalArgumentException("Range vacío");
        }

        String[] tokens = range.split(",");

        for (String token : tokens) {

            token = token.trim();

            if (token.contains("-")) {

                String[] parts = token.split("-");

                int start = Integer.parseInt(parts[0].trim());
                int end = Integer.parseInt(parts[1].trim());

                if (start > end) {
                    throw new IllegalArgumentException("Rango inválido: " + token);
                }

                for (int i = start; i <= end; i++) {
                    pages.add(i);
                }

            } else {
                pages.add(Integer.parseInt(token));
            }
        }

        return pages;
    }

    private void validatePage(int page, int maxPages) {
        if (page < 1 || page > maxPages) {
            throw new IllegalArgumentException("Página fuera de rango: " + page);
        }
    }
}