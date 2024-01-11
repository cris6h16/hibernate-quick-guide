package org.example.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "course_enrollments",
        schema = "tienda",
        catalog = "tienda",
        indexes = {
                @Index(name = "weight_id_idx", columnList = "id", unique = true)
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "student_course_unique",
                        columnNames = {"student_id", "course_id"}
                )
        }
)
public class CourseEnrollmentsEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String student_id;

    @Column(unique = true)
    private String course_id;


}
