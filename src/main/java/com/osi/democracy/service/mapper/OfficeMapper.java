package com.osi.democracy.service.mapper;

import com.osi.democracy.domain.Office;
import com.osi.democracy.service.dto.OfficeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Office} and its DTO {@link OfficeDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfficeMapper extends EntityMapper<OfficeDTO, Office> {}
