// src/main/java/com/example/spring_cap/ContactMessageRepository.java
package com.example.spring_cap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}