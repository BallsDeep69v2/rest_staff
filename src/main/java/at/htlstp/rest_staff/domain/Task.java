package at.htlstp.rest_staff.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@AllArgsConstructor
@Getter
@Table(name = "tasks")
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue
    private Integer id;

    @NonNull
    private String description;

    @ManyToOne
    private Employee employee;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "finished_date")
    private LocalDate finished;

    @Column(name = "hours_worked")
    private int hoursWorked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return id != null && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
