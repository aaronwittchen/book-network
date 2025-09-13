package com.onion.book_network.book;

import java.util.ArrayList;
import java.util.List;

import com.onion.book_network.common.BaseEntity;
import com.onion.book_network.feedback.Feedback;
import com.onion.book_network.history.BookTransactionHistory;
import com.onion.book_network.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.BatchSize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {
   
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author name is required")
    private String authorName;
    
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    private String isbn;
    
    private String synopsis;
    
    private String bookCover;
    
    private boolean archived;
    
    private boolean shareable;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private List<Feedback> feedbacks = new ArrayList<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private List<BookTransactionHistory> histories = new ArrayList<>();
    
    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        
        double rate = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        
        // Round to 1 decimal place
        return Math.round(rate * 10.0) / 10.0;
    }
    
    @Transient
    public int getTotalFeedbacks() {
        return feedbacks != null ? feedbacks.size() : 0;
    }
    
    @Transient
    public boolean isAvailableForSharing() {
        return shareable && !archived;
    }
}