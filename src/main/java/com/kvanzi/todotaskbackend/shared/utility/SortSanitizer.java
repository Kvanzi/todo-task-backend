package com.kvanzi.todotaskbackend.shared.utility;

import java.util.List;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SortSanitizer {
    public static Sort sanitize(Sort sort, UnaryOperator<String> resolver) {
        List<Sort.Order> orders = sort.stream()
            .map(order -> new Sort.Order(order.getDirection(), resolver.apply(order.getProperty())))
            .toList();
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }
}
