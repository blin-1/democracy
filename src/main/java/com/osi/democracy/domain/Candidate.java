package com.osi.democracy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.osi.democracy.domain.enumeration.Party;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Candidate.
 */
@Entity
@Table(name = "candidates")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Candidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "first_name", length = 128, nullable = false)
    private String firstName;

    @NotNull
    @Size(max = 128)
    @Column(name = "last_name", length = 128, nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "party", nullable = false)
    private Party party;

    @Lob
    @Column(name = "pic", nullable = false)
    private byte[] pic;

    @NotNull
    @Column(name = "pic_content_type", nullable = false)
    private String picContentType;

    @Lob
    @Column(name = "bio", nullable = false)
    private String bio;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "candidate")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "issue", "candidate" }, allowSetters = true)
    private Set<Position> positions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "candidates" }, allowSetters = true)
    private Office office;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Candidate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Candidate firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Candidate lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Candidate email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Party getParty() {
        return this.party;
    }

    public Candidate party(Party party) {
        this.setParty(party);
        return this;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public byte[] getPic() {
        return this.pic;
    }

    public Candidate pic(byte[] pic) {
        this.setPic(pic);
        return this;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public String getPicContentType() {
        return this.picContentType;
    }

    public Candidate picContentType(String picContentType) {
        this.picContentType = picContentType;
        return this;
    }

    public void setPicContentType(String picContentType) {
        this.picContentType = picContentType;
    }

    public String getBio() {
        return this.bio;
    }

    public Candidate bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Position> getPositions() {
        return this.positions;
    }

    public void setPositions(Set<Position> positions) {
        if (this.positions != null) {
            this.positions.forEach(i -> i.setCandidate(null));
        }
        if (positions != null) {
            positions.forEach(i -> i.setCandidate(this));
        }
        this.positions = positions;
    }

    public Candidate positions(Set<Position> positions) {
        this.setPositions(positions);
        return this;
    }

    public Candidate addPosition(Position position) {
        this.positions.add(position);
        position.setCandidate(this);
        return this;
    }

    public Candidate removePosition(Position position) {
        this.positions.remove(position);
        position.setCandidate(null);
        return this;
    }

    public Office getOffice() {
        return this.office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public Candidate office(Office office) {
        this.setOffice(office);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidate)) {
            return false;
        }
        return id != null && id.equals(((Candidate) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Candidate{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", party='" + getParty() + "'" +
            ", pic='" + getPic() + "'" +
            ", picContentType='" + getPicContentType() + "'" +
            ", bio='" + getBio() + "'" +
            "}";
    }
}
