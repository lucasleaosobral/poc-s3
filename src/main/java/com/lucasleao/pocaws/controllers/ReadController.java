package com.lucasleao.pocaws.controllers;


import com.amazonaws.services.s3.model.S3Object;
import com.lucasleao.pocaws.core.domain.utils.FileUtils;
import com.lucasleao.pocaws.services.ReadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/read")
public class ReadController {

    private final ReadService readService;
    private final FileUtils fileUtils;

    public ReadController(ReadService readService, FileUtils fileUtils) {
        this.readService = readService;
        this.fileUtils = fileUtils;
    }


    @Operation(summary = "Get the file URL", description = "returns the file URL",
            responses = {
                    @ApiResponse(description = "URL in the body", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @GetMapping("{fileId}")
    public ResponseEntity getFileUrl(@PathVariable String fileId) {

        String fileUrl = readService.findFileUrl(fileId);

        return new ResponseEntity(fileUrl, null, HttpStatus.OK);
    }

    @Operation(summary = "Download a file", description = "Download a file from the server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping(value = "/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileId) {

        S3Object s3Object = readService.downloadFile(fileId);

        byte[] content = fileUtils.getBytesFromFile(s3Object);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", fileUtils.getFileContentType(content));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .body(new ByteArrayResource(content));

    }


    @Operation(summary = "list uploaded files", description = "returns the file list",
            responses = {
                    @ApiResponse(description = "array of URLs", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @GetMapping(value = "/list")
    public ResponseEntity listFiles() {
        List<String> filesList = readService.listFiles();
        return new ResponseEntity(filesList, HttpStatus.OK);
    }
}
