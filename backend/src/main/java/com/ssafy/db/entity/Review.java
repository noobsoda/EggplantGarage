package com.ssafy.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Review extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String content;
    private Double score;
    private boolean isSeller;
    private boolean isVisible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
