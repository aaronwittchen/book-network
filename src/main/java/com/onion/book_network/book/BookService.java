package com.onion.book_network.book;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.onion.book_network.common.PageResponse;
import com.onion.book_network.exception.OperationNotPermittedException;
import com.onion.book_network.file.FileStorageService;
import com.onion.book_network.history.BookTransactionHistory;
import com.onion.book_network.history.BookTransactionHistoryRepository;
import com.onion.book_network.user.User;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public BookResponse save(BookRequest request, Authentication connectedUser) {
        User user = getCurrentUser(connectedUser);
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookResponse(
            savedBook, 
            "Book created successfully with id: " + savedBook.getId()
        );
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(book -> {
                    // Initialize any required lazy-loaded collections
                    if (book.getOwner() != null) {
                        book.getOwner().getFirstName(); // Trigger lazy loading
                    }
                    return bookMapper.toBookResponse(book);
                })
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        Pageable pageable = buildPageable(page, size);
        User user = getCurrentUser(connectedUser);
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        
        // Initialize any required lazy-loaded collections
        List<Book> booksList = books.getContent();
        if (!booksList.isEmpty()) {
            // This will initialize the owner for each book
            booksList.forEach(book -> {
                if (book.getOwner() != null) {
                    book.getOwner().getFirstName(); // Trigger lazy loading
                }
            });
        }
        
        List<BookResponse> booksResponse = booksList.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(booksResponse, books.getNumber(), books.getSize(),
                books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        Pageable pageable = buildPageable(page, size);
        User user = getCurrentUser(connectedUser);
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(booksResponse, books.getNumber(), books.getSize(),
                books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = getCurrentUser(connectedUser);
        Pageable pageable = buildPageable(page, size);
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = getCurrentUser(connectedUser);
        Pageable pageable = buildPageable(page, size);
        Page<BookTransactionHistory> allReturnedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(booksResponse,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast());
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = getCurrentUser(connectedUser);
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = getCurrentUser(connectedUser);
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }
        User user = getCurrentUser(connectedUser);
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }

        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = getCurrentUser(connectedUser);
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = getCurrentUser(connectedUser);
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    @Transactional
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = getCurrentUser(connectedUser);
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can only upload covers for your own books");
        }
        var bookCover = fileStorageService.saveFile(file, user.getId().toString());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    // Helper methods
    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdDate").descending());
    }

    private Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
