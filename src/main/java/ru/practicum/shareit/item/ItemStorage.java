package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    List<Item> getAll(long id);

    Optional<Item> getById(long id);

    List<Item> getSearchItems(String text);

    Item create(Item item);

    Item update(Item item);

    void delete(long id);
}
