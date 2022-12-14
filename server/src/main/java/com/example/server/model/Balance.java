package com.example.server.model;

import javax.persistence.*;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "balances")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "value")
    private Long value;

    @Setter(AccessLevel.NONE)
    @Version
    @Column(name = "version")
    private Integer version;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;


    public Balance(User user) {
        this.user = user;
        this.value = 0L;
    }
}