// src/main/java/com/example/spring_cap/ContactMessage.java
package com.example.spring_cap;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "contact_messages")
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String message;
}