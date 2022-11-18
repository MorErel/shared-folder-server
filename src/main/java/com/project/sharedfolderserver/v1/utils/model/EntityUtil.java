package com.project.sharedfolderserver.v1.utils.model;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
/**
 * Entity utils
 */
public class EntityUtil {

    public final static ModelMapper mapper = new ModelMapper();

    public static <T,U> U convert(T object, Class<U> clazz) {
        log.debug("converting {} to {}", object, clazz);
        return mapper.map(object, clazz);
    }
}
