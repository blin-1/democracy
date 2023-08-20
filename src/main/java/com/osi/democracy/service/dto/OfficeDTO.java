package com.osi.democracy.service.dto;

import com.osi.democracy.domain.enumeration.State;
import com.osi.democracy.domain.enumeration.YesNo;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.osi.democracy.domain.Office} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfficeDTO implements Serializable {

    private Long id;

    private State state;

    @Size(max = 128)
    private String municipality;

    private YesNo federal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public YesNo getFederal() {
        return federal;
    }

    public void setFederal(YesNo federal) {
        this.federal = federal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfficeDTO)) {
            return false;
        }

        OfficeDTO officeDTO = (OfficeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, officeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfficeDTO{" +
            "id=" + getId() +
            ", state='" + getState() + "'" +
            ", municipality='" + getMunicipality() + "'" +
            ", federal='" + getFederal() + "'" +
            "}";
    }
}
