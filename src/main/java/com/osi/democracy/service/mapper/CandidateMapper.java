package com.osi.democracy.service.mapper;

import com.osi.democracy.domain.Candidate;
import com.osi.democracy.domain.Office;
import com.osi.democracy.service.dto.CandidateDTO;
import com.osi.democracy.service.dto.OfficeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Candidate} and its DTO {@link CandidateDTO}.
 */
@Mapper(componentModel = "spring")
public interface CandidateMapper extends EntityMapper<CandidateDTO, Candidate> {
    @Mapping(target = "office", source = "office", qualifiedByName = "officeId")
    CandidateDTO toDto(Candidate s);

    @Named("officeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OfficeDTO toDtoOfficeId(Office office);
}
