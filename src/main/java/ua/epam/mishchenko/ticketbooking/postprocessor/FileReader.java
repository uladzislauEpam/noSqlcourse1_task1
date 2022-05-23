package ua.epam.mishchenko.ticketbooking.postprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileReader {

    private File file;

    public Map<String, String> readPreparedDataFromFile() {
        String delimiter = "=";
        Map<String, String> map = new HashMap<>();
        try(Stream<String> lines = Files.lines(file.toPath())){
            lines.filter(line -> line.contains(delimiter)).forEach(
                    line -> map.putIfAbsent(line.split(delimiter)[0], line.split(delimiter)[1])
            );
        } catch (IOException e) {
            throw new RuntimeException("Can not to retrieve prepared data from file", e);
        }
        return map;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
