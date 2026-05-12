package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT * FROM (" +
            "SELECT b.*, ROW_NUMBER() OVER (PARTITION BY item_id ORDER BY start_date DESC) as rn " +
            "FROM bookings b WHERE item_id IN :itemIds AND status = 'APPROVED' AND start_date <= :now" +
            ") t1 WHERE rn = 1 " +
            "UNION ALL " +
            "SELECT * FROM (" +
            "SELECT b.*, ROW_NUMBER() OVER (PARTITION BY item_id ORDER BY start_date ASC) as rn " +
            "FROM bookings b WHERE item_id IN :itemIds AND status = 'APPROVED' AND start_date > :now" +
            ") t2 WHERE rn = 1", nativeQuery = true)
    List<Booking> findLastAndNextBookings(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);
}