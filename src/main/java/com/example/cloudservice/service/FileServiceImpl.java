package com.example.cloudservice.service;

import com.example.cloudservice.dto.FileResponse;
import com.example.cloudservice.exceptions.*;
import com.example.cloudservice.model.AuthToken;
import com.example.cloudservice.dto.NewFilenameRequest;
import com.example.cloudservice.model.entity.FileEntity;
import com.example.cloudservice.repository.FileRepository;
import com.example.cloudservice.security.JwtTokenUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.cloudservice.exceptions.MessageConstant.*;
import static java.util.Objects.isNull;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final AuthToken token;
    private final JwtTokenUtils jwtTokenUtils;


    @Override
    public String uploadFile(String authToken, MultipartFile file) {
        try {
            FileEntity doublicateFilename = fileRepository.findByFilename(file.getOriginalFilename());
            if (isNull(doublicateFilename)) {
                fileRepository.save(new FileEntity(file.getOriginalFilename(),
                        file.getSize(), file.getContentType(), file.getBytes()));
                log.info("Successful upload file: " + file.getOriginalFilename());
                return SUCCESS_UPLOAD;
            }
            log.error("Error upload file: " + file.getOriginalFilename() + ". Filename is not uniq!");
            throw new FileUploadException(ERROR_UPLOAD_NOT_UNIQ_FILE);
        } catch (IOException e) {
            log.error("Error upload file: " + file.getOriginalFilename() + ". Please try again!");
            throw new FileUploadException(ERROR_UPLOAD_FILE);
        }
    }

    @Override
    public String deleteFile(String authToken, String filename) {
        FileEntity file = fileRepository.findByFilename(filename);
        if (isNull(file)) {
            throw new DeleteFileException(ERROR_DELETE_FILENAME);
        }
        fileRepository.delete(file);
        return SUCCESS_DELETE;
    }


    @Override
    public ResponseEntity<FileEntity> getFile(String authToken, String filename) {
        FileEntity file = fileRepository.findByFilename(filename);
        return prepareFilesInfo(file);
    }


    private ResponseEntity prepareFilesInfo(FileEntity file) {
        return ResponseEntity.ok()
                .header(HttpHeaders
                        .CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.valueOf(file.getType()))
                .body(file.getBody());
    }

    @Override
    public String renameFile(String authToken, String filename, NewFilenameRequest nf) {
        FileEntity file = fileRepository.findByFilename(filename);
        if (isNull(file)) {
            log.error("Error rename file: " + filename + ". Please try again!");
            throw new RenameFileException(ERROR_RENAME_FILENAME);
        }
        file.setFilename(nf.getFilename());
        fileRepository.save(file);
        log.info("Successful rename file: " + filename);
        return SUCCESS_RENAME;
    }

    @Override
    public List<FileResponse> getAllFiles(String authToken, Integer limit) {
        List<FileEntity> fileList = fileRepository.findAll();
        if (fileList.isEmpty()) {
            throw new GettingFileListException(ERROR_GETTING_FILE_LIST);
        }
        return fileList.stream().map(f -> new FileResponse(f.getFilename(), f.getSize()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
