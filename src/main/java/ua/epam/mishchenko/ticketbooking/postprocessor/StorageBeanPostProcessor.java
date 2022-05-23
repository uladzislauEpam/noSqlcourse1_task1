package ua.epam.mishchenko.ticketbooking.postprocessor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import ua.epam.mishchenko.ticketbooking.db.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StorageBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LogManager.getLogger(StorageBeanPostProcessor.class);

    private FileReader fileReader;

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
                Map<String, String> inMemoryStorage = fileReader.readPreparedDataFromFile();
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

    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }
}
