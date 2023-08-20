package com.osi.democracy.service.mapper;


import org.junit.jupiter.api.BeforeEach;

class CandidateMapperTest {

    private CandidateMapper candidateMapper;

    @BeforeEach
    public void setUp() {
        candidateMapper = new CandidateMapperImpl();
    }
}
