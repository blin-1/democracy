package com.osi.democracy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.osi.democracy.domain.enumeration.State;
import com.osi.democracy.domain.enumeration.YesNo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Size(max = 128)
    @Column(name = "municipality", length = 128)
    private String municipality;

    @Enumerated(EnumType.STRING)
    @Column(name = "federal")
    private YesNo federal;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "office")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "issues", "office" }, allowSetters = true)
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

    public YesNo getFederal() {
        return this.federal;
    }

    public Office federal(YesNo federal) {
        this.setFederal(federal);
        return this;
    }

    public void setFederal(YesNo federal) {
        this.federal = federal;
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
            ", state='" + getState() + "'" +
            ", municipality='" + getMunicipality() + "'" +
            ", federal='" + getFederal() + "'" +
            "}";
    }
}
