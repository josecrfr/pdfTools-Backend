package com.pdf.tools.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfCompresService {

    public byte[] compress(MultipartFile file, int targetKb) {

        try {

            // PDF temporal entrada
            Path input = Files.createTempFile("input-", ".pdf");

            // PDF temporal salida
            Path output = Files.createTempFile("output-", ".pdf");

            Files.write(input, file.getBytes());

            // Leer script desde resources
            ClassPathResource resource = new ClassPathResource("python/compress.py");

            InputStream scriptInputStream = resource.getInputStream();

            // Crear archivo temporal REAL para python
            Path tempScript = Files.createTempFile("compress-", ".py");

            Files.copy(
                    scriptInputStream,
                    tempScript,
                    StandardCopyOption.REPLACE_EXISTING);

            // Ejecutar Python
            ProcessBuilder pb = new ProcessBuilder(
                    "./venv/bin/python",
                    tempScript.toString(),
                    input.toString(),
                    output.toString(),
                    String.valueOf(targetKb));

            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Logs Python
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()));

            StringBuilder logs = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            System.out.println("PYTHON LOGS:\n" + logs);

            if (exitCode != 0) {
                throw new RuntimeException(
                        "Python falló:\n" + logs);
            }

            // Leer PDF final
            byte[] result = Files.readAllBytes(output);

            // Limpiar temporales
            Files.deleteIfExists(input);
            Files.deleteIfExists(output);
            Files.deleteIfExists(tempScript);

            return result;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error ejecutando Python", e);
        }
    }
}