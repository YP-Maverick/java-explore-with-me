package ru.practicum.ewm.main.compilation.model;

import lombok.*;
import org.hibernate.Hibernate;

import jakarta.persistence.*;
import ru.practicum.ewm.main.events.model.Event;

import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Generated
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean pinned;

    @ManyToMany
    private Set<Event> events;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Compilation that = (Compilation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Compilation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", pinned=" + pinned +
                '}';
    }
}