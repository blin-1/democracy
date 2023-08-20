package com.osi.democracy.service.mapper;


import org.junit.jupiter.api.BeforeEach;

class IssueMapperTest {

    private IssueMapper issueMapper;

    @BeforeEach
    public void setUp() {
        issueMapper = new IssueMapperImpl();
    }
}
