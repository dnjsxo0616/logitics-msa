package com.iftrue.hub.application.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class HubCreateRequestDto {

    @NotBlank(message = "허브 이름은 필수입니다.")
    @Size(max = 100, message = "허브 이름은 최대 100자입니다.")
    private String name;

    @NotBlank(message = "허브 주소지는 필수입니다.")
    @Size(max = 255, message = "허브 주소지는 최대 255자입니다.")
    private String address;

    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90", message = "위도는 -90~90 사이여야 합니다.")
    @DecimalMax(value = "90", message = "위도는 -90~90 사이여야 합니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180", message = "경도는 -180~180 사이여야 합니다.")
    @DecimalMax(value = "180", message = "경도는 -180~180 사이여야 합니다.")
    private BigDecimal longitude;
}
