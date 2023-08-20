package com.osi.democracy.service.mapper;

import com.osi.democracy.domain.Candidate;
import com.osi.democracy.domain.Issue;
import com.osi.democracy.service.dto.CandidateDTO;
import com.osi.democracy.service.dto.IssueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Issue} and its DTO {@link IssueDTO}.
 */
@Mapper(componentModel = "spring")
public interface IssueMapper extends EntityMapper<IssueDTO, Issue> {
    @Mapping(target = "candidate", source = "candidate", qualifiedByName = "candidateId")
    IssueDTO toDto(Issue s);

    @Named("candidateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CandidateDTO toDtoCandidateId(Candidate candidate);
}
