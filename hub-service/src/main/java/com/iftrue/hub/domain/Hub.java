package com.iftrue.hub.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_hub", schema = "hub_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    public static Hub create(
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        Hub hub = new Hub();
        hub.name = name;
        hub.address = address;
        hub.latitude = latitude;
        hub.longitude = longitude;

        return hub;
    }

    public void update(
            String name, String address, BigDecimal latitude, BigDecimal longitude
    ) {
        if (name != null) { this.name = name; }
        if (address != null) { this.address = address; }
        if (latitude != null) { this.latitude = latitude; }
        if (longitude != null) { this.longitude = longitude; }
    }
}
