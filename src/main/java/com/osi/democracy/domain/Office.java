package com.osi.democracy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.osi.democracy.domain.enumeration.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Office.
 */
@Entity
@Table(name = "offices")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Office implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 128)
    @Column(name = "name", length = 128)
    private String name;

    @Size(max = 128)
    @Column(name = "municipality", length = 128)
    private String municipality;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Column(name = "election_date")
    private Instant electionDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "office")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "positions", "office" }, allowSetters = true)
    private Set<Candidate> candidates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Office id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Office name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMunicipality() {
        return this.municipality;
    }

    public Office municipality(String municipality) {
        this.setMunicipality(municipality);
        return this;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public State getState() {
        return this.state;
    }

    public Office state(State state) {
        this.setState(state);
        return this;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Instant getElectionDate() {
        return this.electionDate;
    }

    public Office electionDate(Instant electionDate) {
        this.setElectionDate(electionDate);
        return this;
    }

    public void setElectionDate(Instant electionDate) {
        this.electionDate = electionDate;
    }

    public Set<Candidate> getCandidates() {
        return this.candidates;
    }

    public void setCandidates(Set<Candidate> candidates) {
        if (this.candidates != null) {
            this.candidates.forEach(i -> i.setOffice(null));
        }
        if (candidates != null) {
            candidates.forEach(i -> i.setOffice(this));
        }
        this.candidates = candidates;
    }

    public Office candidates(Set<Candidate> candidates) {
        this.setCandidates(candidates);
        return this;
    }

    public Office addCandidate(Candidate candidate) {
        this.candidates.add(candidate);
        candidate.setOffice(this);
        return this;
    }

    public Office removeCandidate(Candidate candidate) {
        this.candidates.remove(candidate);
        candidate.setOffice(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Office)) {
            return false;
        }
        return id != null && id.equals(((Office) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Office{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", municipality='" + getMunicipality() + "'" +
            ", state='" + getState() + "'" +
            ", electionDate='" + getElectionDate() + "'" +
            "}";
    }
}
