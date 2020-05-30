package io.millers.learntestcontainers.todo;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ItemStore {

    private ItemRepo repo;

    public ItemStore(ItemRepo repo) {
        this.repo = repo;
    }

    public Item save(@Valid Item item) {
        return repo.save(item);
    }

    public Optional<Item> get(String id) {
        return repo.findById(id);
    }

    public List<Item> findByListId(String listId) {
        return repo.findAllByListIdOrderByImportanceDesc(listId);
    }

}
