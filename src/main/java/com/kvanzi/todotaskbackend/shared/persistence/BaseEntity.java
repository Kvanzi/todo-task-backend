package com.kvanzi.todotaskbackend.shared.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter(AccessLevel.PROTECTED)
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private @Nullable UUID id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private @Nullable Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private @Nullable Instant updatedAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        Class<?> objEffectiveClass = obj instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : obj.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();

        if (objEffectiveClass != thisEffectiveClass) {
            return false;
        }

        BaseEntity that = (BaseEntity) obj;

        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}
