package com.project.sharedfolderserver.v1.utils.model;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EntityUtil {

    public final static ModelMapper mapper = new ModelMapper();

    public <T> Object convert(T object, Class<?> clazz) {
        log.info("converting " + object + " to " + clazz);
        return mapper.map(object, clazz);
    }
}
