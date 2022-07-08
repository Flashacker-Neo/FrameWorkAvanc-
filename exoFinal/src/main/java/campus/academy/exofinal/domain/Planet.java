package campus.academy.exofinal.domain;

import campus.academy.exofinal.domain.enumeration.PlanetTypes;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Planet.
 */
@Entity
@Table(name = "planet")
public class Planet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Min(value = 1L)
    @Column(name = "distance", nullable = false)
    private Long distance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PlanetTypes type;

    @Column(name = "satellite")
    private Instant satellite;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Planet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Planet name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDistance() {
        return this.distance;
    }

    public Planet distance(Long distance) {
        this.setDistance(distance);
        return this;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public PlanetTypes getType() {
        return this.type;
    }

    public Planet type(PlanetTypes type) {
        this.setType(type);
        return this;
    }

    public void setType(PlanetTypes type) {
        this.type = type;
    }

    public Instant getSatellite() {
        return this.satellite;
    }

    public Planet satellite(Instant satellite) {
        this.setSatellite(satellite);
        return this;
    }

    public void setSatellite(Instant satellite) {
        this.satellite = satellite;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Planet)) {
            return false;
        }
        return id != null && id.equals(((Planet) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Planet{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", distance=" + getDistance() +
            ", type='" + getType() + "'" +
            ", satellite='" + getSatellite() + "'" +
            "}";
    }
}
