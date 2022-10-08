package com.project.sharedfolderserver.v1.utils.model;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EntityUtil {

    public final static ModelMapper mapper = new ModelMapper();

    public <T> Object convert(T object, Class<?> clazz){
        return mapper.map(object, clazz);
    }
}
