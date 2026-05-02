package ru.practicum.shareit.item.dao;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class ItemInMemoryStorage implements ItemStorage {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public Item create(Item item) {
        item.setId(idGenerator.incrementAndGet());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAll(long ownerId) {
        return items.values().stream().filter(item -> item.getOwner() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<Item> getSearchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> (
                        (item.getName().toLowerCase().contains(lowerCaseText)
                                || item.getDescription().toLowerCase().contains(lowerCaseText)))
                        && item.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item newItem) {
            items.put(newItem.getId(), newItem);
            return newItem;
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }
}
