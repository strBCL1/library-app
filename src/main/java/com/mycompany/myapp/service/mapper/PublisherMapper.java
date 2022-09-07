package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.service.dto.PublisherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Publisher} and its DTO {@link PublisherDTO}.
 */
@Mapper(componentModel = "spring")
public interface PublisherMapper extends EntityMapper<PublisherDTO, Publisher> {}
