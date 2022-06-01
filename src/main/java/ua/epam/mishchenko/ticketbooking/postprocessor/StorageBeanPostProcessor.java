package ua.epam.mishchenko.ticketbooking.postprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import ua.epam.mishchenko.ticketbooking.db.Storage;

import java.util.Map;

/**
 * The type Storage bean post processor.
 */
public class StorageBeanPostProcessor implements BeanPostProcessor {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(StorageBeanPostProcessor.class);

    /**
     * The File reader.
     */
    private FileReader fileReader;

    /**
     * Post process before initialization object.
     *
     * @param bean     the bean
     * @param beanName the bean name
     * @return the object
     * @throws BeansException the beans exception
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return process(bean);
    }

    /**
     * Post process after initialization object.
     *
     * @param bean     the bean
     * @param beanName the bean name
     * @return the object
     * @throws BeansException the beans exception
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     * Process object.
     *
     * @param bean the bean
     * @return the object
     */
    private Object process(Object bean) {
        log.info("{}: processing bean of type {} during initialization",
                this.getClass().getSimpleName(), bean.getClass().getName());
        if (bean instanceof Storage) {
            log.info("{}: inserting prepared data in bean of type {}",
                    this.getClass().getSimpleName(), bean.getClass().getName());
            try {
                Map<String, String> inMemoryStorage = fileReader.readPreparedDataFromFile();
                Storage storage = (Storage) bean;
                storage.setInMemoryStorage(inMemoryStorage);
                log.info("{}: successfully inserting prepared data in bean of type {}",
                        this.getClass().getSimpleName(), bean.getClass().getName());
                return storage;
            } catch (RuntimeException e) {
                log.error("{}: error while inserting prepared data in bean of type {}",
                        this.getClass().getSimpleName(), bean.getClass().getName(), e);
            }
        }
        return bean;
    }

    /**
     * Sets file reader.
     *
     * @param fileReader the file reader
     */
    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }
}
