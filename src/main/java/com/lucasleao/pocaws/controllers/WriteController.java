package com.lucasleao.pocaws.controllers;


import com.lucasleao.pocaws.services.WriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/write")
public class WriteController {

    private final WriteService writeService;

    public WriteController(WriteService writeService) {
        this.writeService = writeService;
    }

    @Operation(summary = "Upload a file", description = "Upload a file to S3",
            responses = {
                    @ApiResponse(description = "File uploaded successfully", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile( @Parameter(description = "File to upload") @RequestPart(value = "file")
                                          @Schema(type = "string", format = "binary") MultipartFile file) {

        String url = writeService.process(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", url);

        return new ResponseEntity(null, headers, HttpStatus.OK);
    }

    @Operation(summary = "Delete a file", description = "Delete file from S3 and DB",
            responses = {
                    @ApiResponse(description = "File deleted successfully", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @DeleteMapping
    public ResponseEntity deleteFile( @RequestParam("fileId") String fileId ) {

        writeService.deleteFile(fileId);

        return new ResponseEntity( HttpStatus.OK);
    }
}
