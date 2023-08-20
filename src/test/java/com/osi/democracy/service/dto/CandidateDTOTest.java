package com.osi.democracy.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.osi.democracy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CandidateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CandidateDTO.class);
        CandidateDTO candidateDTO1 = new CandidateDTO();
        candidateDTO1.setId(1L);
        CandidateDTO candidateDTO2 = new CandidateDTO();
        assertThat(candidateDTO1).isNotEqualTo(candidateDTO2);
        candidateDTO2.setId(candidateDTO1.getId());
        assertThat(candidateDTO1).isEqualTo(candidateDTO2);
        candidateDTO2.setId(2L);
        assertThat(candidateDTO1).isNotEqualTo(candidateDTO2);
        candidateDTO1.setId(null);
        assertThat(candidateDTO1).isNotEqualTo(candidateDTO2);
    }
}
