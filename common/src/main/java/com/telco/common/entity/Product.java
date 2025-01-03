package com.telco.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prod_bas")
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @Column(name = "prod_id")
    private String prodId;

    @Column(name = "prod_nm")
    private String prodNm;
}