package ua.epam.mishchenko.ticketbooking.postprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The type File reader.
 */
public class FileReader {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(FileReader.class);

    /**
     * The Path to the file.
     */
    private String path;

    /**
     * Read prepared data from file map.
     *
     * @return the map
     */
    public Map<String, String> readPreparedDataFromFile() {
        log.info("Trying to read prepared data from file: {}", path);

        String delimiter = "=";
        Map<String, String> map = new HashMap<>();
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (Stream<String> lines = Files.lines(classPathResource.getFile().toPath())) {
            lines.filter(line -> line.contains(delimiter)).forEach(
                    line -> map.putIfAbsent(line.split(delimiter)[0], line.split(delimiter)[1])
            );
        } catch (IOException e) {
            log.warn("Can not to retrieve prepared data from file: {}", path);
            throw new RuntimeException("Can not to retrieve prepared data from file", e);
        }

        log.info("Data read successfully");
        return map;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
