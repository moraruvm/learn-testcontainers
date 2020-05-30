package io.millers.learntestcontainers.todo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

interface ItemRepo extends MongoRepository<Item, String> {
    List<Item> findAllByListIdOrderByImportanceDesc(String listId);
}
