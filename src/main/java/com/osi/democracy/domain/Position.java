package com.osi.democracy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Position.
 */
@Entity
@Table(name = "statements")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "statement", nullable = false)
    private String statement;

    @ManyToOne(fetch = FetchType.LAZY)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "positions", "office" }, allowSetters = true)
    private Candidate candidate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Position id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return this.statement;
    }

    public Position statement(String statement) {
        this.setStatement(statement);
        return this;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Issue getIssue() {
        return this.issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Position issue(Issue issue) {
        this.setIssue(issue);
        return this;
    }

    public Candidate getCandidate() {
        return this.candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Position candidate(Candidate candidate) {
        this.setCandidate(candidate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        return id != null && id.equals(((Position) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Position{" +
            "id=" + getId() +
            ", statement='" + getStatement() + "'" +
            "}";
    }
}
