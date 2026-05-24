package com.pdf.tools.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfCompresService {

    // 🔥 IMPORTANTE: ruta al Python del venv
    private static final String PYTHON_EXECUTABLE = "/home/jcruzf/iLovePdf/pdfTools-Backend/venv/bin/python";

    public byte[] compress(MultipartFile file, int targetKb) {

        try {
            // 1. Crear archivos temporales
            Path input = Files.createTempFile("input-", ".pdf");
            Path output = Files.createTempFile("output-", ".pdf");

            Files.write(input, file.getBytes());

            // 2. Ruta del script Python desde resources
            String scriptPath = getClass()
                    .getClassLoader()
                    .getResource("python/compress.py")
                    .getPath()
                    .replace("%20", " ");

            // 3. Proceso Python
            ProcessBuilder pb = new ProcessBuilder(
                    "./venv/bin/python",
                    scriptPath.toString(),
                    input.toString(),
                    output.toString(),
                    String.valueOf(targetKb));

            pb.redirectErrorStream(true);

            Process process = pb.start();

            // 4. Leer logs Python
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder logs = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            System.out.println("PYTHON LOGS:\n" + logs);

            if (exitCode != 0) {
                throw new RuntimeException("Python falló:\n" + logs);
            }

            // 5. Leer PDF generado
            return Files.readAllBytes(output);

        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando Python", e);
        }
    }
}