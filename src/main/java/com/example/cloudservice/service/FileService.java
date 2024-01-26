package com.example.cloudservice.service;

import com.example.cloudservice.dto.FileResponse;
import com.example.cloudservice.dto.NewFilenameRequest;
import com.example.cloudservice.model.entity.FileEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String uploadFile(String authToken, MultipartFile file);

    String deleteFile(String authToken, String filename);

    ResponseEntity<FileEntity> getFile(String authToken, String filename);

    String renameFile(String authToken, String filename, NewFilenameRequest newFilename);

    List<FileResponse> getAllFiles(String authToken, Integer limit);
}