package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getSearchItems_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> result = itemService.getSearchItems("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void getSearchItems_whenTextIsValid_thenReturnList() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setOwner(owner);

        when(itemRepository.search("Дрель")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getSearchItems("Дрель");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void create_whenUserFound_thenReturnItemDto() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        NewItemDto dto = new NewItemDto();
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Item");
        savedItem.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.create(userId, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void create_whenWithRequest_thenSetsRequest() {
        long userId = 1L;
        long requestId = 10L;

        User user = new User();
        user.setId(userId);

        NewItemDto dto = new NewItemDto();
        dto.setName("Item");
        dto.setRequestId(requestId);
        dto.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Item");
        savedItem.setOwner(user);
        savedItem.setRequest(request);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.create(userId, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void delete_whenUserIsOwner_thenDelete() {
        long userId = 1L;
        long itemId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(userId, itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void delete_whenUserIsNotOwner_thenThrowException() {
        long userId = 1L;
        long itemId = 1L;

        User owner = new User();
        owner.setId(2L); // Другой владелец

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(UserValidationException.class, () -> itemService.delete(userId, itemId));
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void update_whenUserIsOwner_thenReturnUpdatedItem() {
        long userId = 1L;
        long itemId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setName("Old");

        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("New");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDto result = itemService.update(userId, itemId, dto);

        assertEquals("New", result.getName());
    }

    @Test
    void getItemById_whenUserIsNotOwner_thenReturnWithoutBookings() {
        long userId = 1L;
        long itemId = 1L;

        User owner = new User();
        owner.setId(2L); // Другой владелец

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemWithBookingDto result = itemService.getItemById(userId, itemId);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getAll_whenItemsFound_thenReturnList() {
        long userId = 1L;
        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndStatusAndStartLessThanEqualOrderByStartDesc(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemInAndStatusAndStartGreaterThanOrderByStartAsc(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemWithBookingDto> result = itemService.getAll(userId);

        assertEquals(1, result.size());
    }

    @Test
    void createComment_whenValid_thenReturnComment() {
        long userId = 1L;
        long itemId = 1L;

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Item");

        NewCommentDto request = new NewCommentDto();
        request.setText("Good");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("Good");
        savedComment.setAuthor(user);
        savedComment.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.createComment(userId, itemId, request);

        assertEquals(1L, result.getId());
        assertEquals("Good", result.getText());
    }

    @Test
    void createComment_whenNoBooking_thenThrowException() {
        long userId = 1L;
        long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThrows(CommentValidationException.class, () -> itemService.createComment(userId, itemId, new NewCommentDto()));
    }
}