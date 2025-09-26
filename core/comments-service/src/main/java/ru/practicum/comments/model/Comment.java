package ru.practicum.comments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.comments.enums.CommentStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "text", nullable = false)
    @Size(min = 5, max = 255)
    String text;

    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

    @ToString.Exclude
    @JoinColumn(name = "author_id")
    Long authorId;

    @ToString.Exclude
    @JoinColumn(name = "event_id")
    Long eventId;

    @Column(name = "author_name", nullable = false)
    String authorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    CommentStatus status = CommentStatus.PENDING;
}
