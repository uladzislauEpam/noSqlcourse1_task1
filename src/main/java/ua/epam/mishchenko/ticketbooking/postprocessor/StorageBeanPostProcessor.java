package ua.epam.mishchenko.ticketbooking.postprocessor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import ua.epam.mishchenko.ticketbooking.db.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class StorageBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LogManager.getLogger(StorageBeanPostProcessor.class);

    private String filePath;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return process(bean);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private Object process(Object bean) {
        LOGGER.log(Level.INFO, "{}: processing bean of type {} during initialization",
                this.getClass().getSimpleName(), bean.getClass().getName());
        if (bean instanceof Storage) {
            LOGGER.log(Level.INFO,
                    "{}: inserting prepared data in bean of type {}",
                    this.getClass().getSimpleName(), bean.getClass().getName());
            try {
                Map<String, String> inMemoryStorage = readPreparedDataFromFile();
                Storage storage = (Storage) bean;
                storage.setInMemoryStorage(inMemoryStorage);
                LOGGER.log(Level.INFO,
                        "{}: successfully inserting prepared data in bean of type {}",
                        this.getClass().getSimpleName(), bean.getClass().getName());
                return storage;
            } catch (RuntimeException e) {
                LOGGER.log(Level.ERROR, "{}: error while inserting prepared data in bean of type {}",
                        this.getClass().getSimpleName(), bean.getClass().getName(), e);
            }
        }
        return bean;
    }

    private Map<String, String> readPreparedDataFromFile() {
        String delimiter = "=";
        Map<String, String> map = new HashMap<>();
        try(Stream<String> lines = Files.lines(Paths.get(filePath))){
            lines.filter(line -> line.contains(delimiter)).forEach(
                    line -> map.putIfAbsent(line.split(delimiter)[0], line.split(delimiter)[1])
            );
        } catch (IOException e) {
            throw new RuntimeException("Can not to retrieve prepared data from file", e);
        }
        return map;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
