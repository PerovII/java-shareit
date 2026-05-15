package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long bookerId, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long ownerId, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            long bookerId, long itemId, BookingStatus status, LocalDateTime now);

    List<Booking> findByItemInAndStatusAndStartLessThanEqualOrderByStartDesc(
            List<Item> items, BookingStatus status, LocalDateTime now);

    List<Booking> findByItemInAndStatusAndStartGreaterThanOrderByStartAsc(
            List<Item> items, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(
            long itemId, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
            long itemId, BookingStatus status, LocalDateTime now);
}