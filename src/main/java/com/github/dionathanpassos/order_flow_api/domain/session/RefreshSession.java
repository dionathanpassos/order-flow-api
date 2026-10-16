package com.github.dionathanpassos.order_flow_api.domain.session;

import com.github.dionathanpassos.order_flow_api.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "refresh_sessions")
@Getter
@Setter
public class RefreshSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant lastUsedAt;

    private Instant revokedAt;

    public static RefreshSession createSession(String tokenHash, User user, Instant expiresAt) {
        RefreshSession session = new RefreshSession();

        session.tokenHash = tokenHash;
        session.user = user;
        session.expiresAt = expiresAt;

        return session;
    }

    public void updateSession(String newTokenHash, Instant expiresAt) {
        this.tokenHash = newTokenHash;
        this.expiresAt = expiresAt;
        this.lastUsedAt = Instant.now();
    }

    public void revokeToken() {
        this.revokedAt = Instant.now();

    }
}
