package campus.academy.exofinal.domain;

import campus.academy.exofinal.domain.enumeration.SpaceEventType;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A SpaceEvent.
 */
@Entity
@Table(name = "space_event")
public class SpaceEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "photo", nullable = false)
    private byte[] photo;

    @NotNull
    @Column(name = "photo_content_type", nullable = false)
    private String photoContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SpaceEventType type;

    @OneToOne
    @JoinColumn(unique = true)
    private Mission mission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SpaceEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SpaceEvent name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public SpaceEvent date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return this.description;
    }

    public SpaceEvent description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public SpaceEvent photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public SpaceEvent photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public SpaceEventType getType() {
        return this.type;
    }

    public SpaceEvent type(SpaceEventType type) {
        this.setType(type);
        return this;
    }

    public void setType(SpaceEventType type) {
        this.type = type;
    }

    public Mission getMission() {
        return this.mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public SpaceEvent mission(Mission mission) {
        this.setMission(mission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceEvent)) {
            return false;
        }
        return id != null && id.equals(((SpaceEvent) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpaceEvent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", date='" + getDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
