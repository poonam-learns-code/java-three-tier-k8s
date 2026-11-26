package com.threetier;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Entity
@Table(name = "items")
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

interface ItemRepository extends JpaRepository<Item, Long> {
}

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
class ApiController {

    private final ItemRepository itemRepository;

    ApiController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/health")
    public ResponseEntity> health() {
        long count = itemRepository.count();
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "database", "connected",
                "itemsCount", count,
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/items")
    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Item saved = itemRepository.save(item);
        return ResponseEntity.ok(saved);
    }
}