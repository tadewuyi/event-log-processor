package com.toba;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileBasedDataSource implements IterableDataSource<String> {

    private static final Logger logger = LogManager.getLogger();
    private String filePath;
    private LineIterator iterator;

    public FileBasedDataSource(String filePath) {
        this.filePath = filePath;
        initialize();
    }

    private void initialize() {
        logger.debug("Opening file: "+filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException(String.format("File path [%s] does not exist", filePath));
        }
        try {
            iterator = FileUtils.lineIterator(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> get() {
        if (iterator.hasNext()) {
            return Optional.of(iterator.nextLine());
        } else {
            try {
                iterator.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        }
    }
}
