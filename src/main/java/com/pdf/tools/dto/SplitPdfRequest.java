package com.pdf.tools.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SplitPdfRequest {
    private MultipartFile file;

    private String mode;
    // "PAGES" | "EVERY_PAGE" | "RANGE"

    private String range;
    // ejemplo: "1-3,5,8-10"

    // getters & setters
}
