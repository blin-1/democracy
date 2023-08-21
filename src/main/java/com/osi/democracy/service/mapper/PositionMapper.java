package com.osi.democracy.service.mapper;

import com.osi.democracy.domain.Candidate;
import com.osi.democracy.domain.Issue;
import com.osi.democracy.domain.Position;
import com.osi.democracy.service.dto.CandidateDTO;
import com.osi.democracy.service.dto.IssueDTO;
import com.osi.democracy.service.dto.PositionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Position} and its DTO {@link PositionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PositionMapper extends EntityMapper<PositionDTO, Position> {
    @Mapping(target = "issue", source = "issue", qualifiedByName = "issueId")
    @Mapping(target = "candidate", source = "candidate", qualifiedByName = "candidateId")
    PositionDTO toDto(Position s);

    @Named("issueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IssueDTO toDtoIssueId(Issue issue);

    @Named("candidateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CandidateDTO toDtoCandidateId(Candidate candidate);
}
