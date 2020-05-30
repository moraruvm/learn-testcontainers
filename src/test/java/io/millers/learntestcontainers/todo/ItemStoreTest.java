package io.millers.learntestcontainers.todo;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DisplayName("ItemStore")
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = ItemStoreTest.MongoDbInitializer.class)
public class ItemStoreTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer().withExposedPorts(8081);
    private ItemStore itemStore;
    private ItemRepo repo;

    @Autowired
    public ItemStoreTest(ItemStore itemStore, ItemRepo repo) {
        this.itemStore = itemStore;
        this.repo = repo;
    }

    @BeforeEach
    public void init() {
        repo.deleteAll();
    }

    @Test
    public void savesAnItem() {
        Item saved = itemStore.save(Item.builder().description("description").build());

        Optional<Item> found = itemStore.get(saved.getId());

        Assertions.assertThat(found).map(Item::getDescription).hasValue("description");
    }

    @Test
    public void throwsAnExceptionWhenDescriptionMissing() {
        Assertions.assertThatThrownBy(() ->
                itemStore.save(Item.builder().build())
        ).isInstanceOf(ValidationException.class);
    }

    @Test
    public void getsItemsInImportanceOrder() {
        Stream.of(10, 5, 1, 8).map(this::itemWithImportance).forEach(itemStore::save);

        List<Item> list = itemStore.findByListId("list");
        List<Integer> descriptions = list.stream().map(Item::getImportance)
                .collect(Collectors.toList());

        Assertions.assertThat(descriptions).containsExactly(10, 8, 5, 1);
    }

    private Item itemWithImportance(int value) {
        return Item.builder().listId("list")
                .description("item" + value).importance(value).build();
    }

    public static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + container.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + container.getFirstMappedPort()

            );
            values.applyTo(configurableApplicationContext);
        }
    }
}

