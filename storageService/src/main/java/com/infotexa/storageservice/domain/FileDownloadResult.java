package com.infotexa.storageservice.domain;

import java.nio.file.Path;

public record FileDownloadResult(Path filePath, String fileName){}
