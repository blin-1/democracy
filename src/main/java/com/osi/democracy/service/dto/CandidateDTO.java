package com.osi.democracy.service.dto;

import com.osi.democracy.domain.enumeration.Party;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.osi.democracy.domain.Candidate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CandidateDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 128)
    private String firstName;

    @NotNull
    @Size(max = 128)
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private Party party;

    @Lob
    private byte[] pic;

    private String picContentType;

    @Lob
    private String bio;

    private OfficeDTO office;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public String getPicContentType() {
        return picContentType;
    }

    public void setPicContentType(String picContentType) {
        this.picContentType = picContentType;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public OfficeDTO getOffice() {
        return office;
    }

    public void setOffice(OfficeDTO office) {
        this.office = office;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandidateDTO)) {
            return false;
        }

        CandidateDTO candidateDTO = (CandidateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, candidateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CandidateDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", party='" + getParty() + "'" +
            ", pic='" + getPic() + "'" +
            ", bio='" + getBio() + "'" +
            ", office=" + getOffice() +
            "}";
    }
}
