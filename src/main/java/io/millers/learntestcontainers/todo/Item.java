package io.millers.learntestcontainers.todo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Document
@Data
@Builder
public class Item {
    @Id
    private String id;
    private String listId;
    @NotNull
    private String description;
    private int importance;
    private LocalDate dueDate;
}
