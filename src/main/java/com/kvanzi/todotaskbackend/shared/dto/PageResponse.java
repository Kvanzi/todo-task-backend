package com.kvanzi.todotaskbackend.shared.dto;

import java.util.Collection;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

@Value
public class PageResponse<T> {
    int totalPages;
    long totalElements;
    @NonNull Collection<T> elements;

    public static <T> PageResponse<T> from(@NonNull Page<@NonNull T> page) {
        return new PageResponse<>(
            page.getTotalPages(),
            page.getTotalElements(),
            page.getContent()
        );
    }
}
