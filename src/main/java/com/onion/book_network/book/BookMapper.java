package com.onion.book_network.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.onion.book_network.file.FileUtils;
import com.onion.book_network.history.BookTransactionHistory;

@Service
@Slf4j
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .isbn(request.isbn())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        String ownerName = null;
        if (book.getOwner() != null) {
            ownerName = book.getOwner().getFirstName() + " " + book.getOwner().getLastName();
        }
        
        String cover = null;
        try {
            cover = FileUtils.readFileFromLocation(book.getBookCover());
        } catch (Exception e) {
            log.warn("Could not load book cover for book id: " + book.getId(), e);
        }
        
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(ownerName)
                .cover(cover)
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
