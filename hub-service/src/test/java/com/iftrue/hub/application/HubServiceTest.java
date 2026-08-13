package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.application.dto.HubUpdateRequestDto;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.response.PageResponse;
import com.iftrue.hub.global.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 허브 서비스 단위 테스트")
public class HubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private HubService hubService;

    @DisplayName("허브를 생성하면 이름 중복을 검증하고 저장한다")
    @Test
    void createHub() {
        UUID hubId = UUID.randomUUID();
        HubCreateRequestDto request = hubCreateRequest("서울특별시 센터");

        given(hubRepository.existsByNameAndDeletedAtIsNull("서울특별시 센터")).willReturn(false);
        given(hubRepository.save(any(Hub.class)))
                .willAnswer(invocation -> {
                    Hub hub = invocation.getArgument(0);
                    ReflectionTestUtils.setField(hub, "id", hubId);
                    return hub;
                });

        HubResponseDto response = hubService.createHub(request);

        assertThat(response.id()).isEqualTo(hubId);
        assertThat(response.name()).isEqualTo("서울특별시 센터");
        assertThat(response.address()).isEqualTo("서울특별시 송파구 송파대로 55");
        assertThat(response.latitude()).isEqualByComparingTo("10.555555");
        assertThat(response.longitude()).isEqualByComparingTo("120.333333");
        verify(hubRepository).save(any(Hub.class));
    }

    @Test
    @DisplayName("허브가 이미 존재하면 허브 생성에 실패한다")
    void createHubDuplicate() {
        HubCreateRequestDto request = hubCreateRequest("서울특별시 센터");
        given(hubRepository.existsByNameAndDeletedAtIsNull("서울특별시 센터")).willReturn(true);

        assertThatThrownBy(() -> hubService.createHub(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_NAME_DUPLICATED.getMessage());

        verify(hubRepository, never()).save(any(Hub.class));
    }

    @Test
    @DisplayName("허브 수정 시 전달된 필드만 부분 반영하고 미전송 이름은 반영되지 않는다")
    void updateHubPartial() {
        UUID hubId = UUID.randomUUID();
        Hub hub = hubWithId("서울특별시 센터", hubId);
        given(hubRepository.findByIdAndDeletedAtIsNull(hubId)).willReturn(Optional.of(hub));

        HubUpdateRequestDto request = hubUpdateRequest(null, "서울특별시 강남구 테헤란로 100", null, null);

        HubResponseDto response = hubService.updateHub(hubId, request);

        assertThat(response.name()).isEqualTo("서울특별시 센터");
        assertThat(response.address()).isEqualTo("서울특별시 강남구 테헤란로 100");
        verify(hubRepository, never()).existsByNameAndDeletedAtIsNull(anyString());
    }

    @Test
    @DisplayName("허브 수정 시 이름에 변경 사항이 없으면 이름 중복 검사를 건너뛴다")
    void updateHubSameNameSkipsDuplicateCheck() {
        UUID hubId = UUID.randomUUID();
        Hub hub = hubWithId("서울특별시 센터", hubId);
        given(hubRepository.findByIdAndDeletedAtIsNull(hubId)).willReturn(Optional.of(hub));

        HubUpdateRequestDto request = hubUpdateRequest("서울특별시 센터", null, null, null);

        hubService.updateHub(hubId, request);

        verify(hubRepository, never()).existsByNameAndDeletedAtIsNull(anyString());
    }

    @Test
    @DisplayName("허브 수정 시 존재하는 다른 허브와 이름이 중복되면 H-002 에러가 발생한다")
    void updateHubDuplicateName() {
        UUID hubId = UUID.randomUUID();
        Hub hub = hubWithId("서울특별시 센터", hubId);
        given(hubRepository.findByIdAndDeletedAtIsNull(hubId)).willReturn(Optional.of(hub));
        given(hubRepository.existsByNameAndDeletedAtIsNull("부산광역시 센터")).willReturn(true);

        HubUpdateRequestDto request = hubUpdateRequest("부산광역시 센터", null, null, null);

        assertThatThrownBy(() -> hubService.updateHub(hubId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_NAME_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("허브 목록을 조회하면 PageResponse로 변환해 반환한다")
    void getHubs() {
        Hub hub1 = hubWithId("서울특별시 센터", UUID.randomUUID());
        Hub hub2 = hubWithId("부산광역시 센터", UUID.randomUUID());
        Pageable pageable = PageRequest.of(0, 10);
        given(hubRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(hub1, hub2), pageable, 2));

        PageResponse<HubResponseDto> response = hubService.getHubs(pageable);

        assertThat(response.content())
                .extracting(HubResponseDto::name)
                .containsExactly("서울특별시 센터", "부산광역시 센터");
        assertThat(response.pageInfo().paginationType()).isEqualTo("OFFSET");
        assertThat(response.pageInfo().page()).isZero();
        assertThat(response.pageInfo().size()).isEqualTo(10);
        assertThat(response.pageInfo().totalElements()).isEqualTo(2);
        assertThat(response.pageInfo().totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("허용되지 않은 size는 10으로 보정되고 기본 정렬은 생성일로 내림차순한다")
    void getHubsClampsSizeAndAppliesDefaultSort() {
        given(hubRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        hubService.getHubs(PageRequest.of(0, 25));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(hubRepository).findAllByDeletedAtIsNull(pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageSize()).isEqualTo(10);
        assertThat(used.getSort().getOrderFor("createdAt").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("허브 검색 시 keyword가 null이면 빈 문자열로 정규화해 조회한다")
    void searchHubNormalizesNullKeyword() {
        given(hubRepository.search(anyString(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        hubService.searchHub(null, PageRequest.of(0, 10));

        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        verify(hubRepository).search(keywordCaptor.capture(), any(Pageable.class));
        assertThat(keywordCaptor.getValue()).isEmpty();
    }

    private HubCreateRequestDto hubCreateRequest(String name) {
        HubCreateRequestDto request = new HubCreateRequestDto();
        ReflectionTestUtils.setField(request, "name", name);
        ReflectionTestUtils.setField(request, "address", "서울특별시 송파구 송파대로 55");
        ReflectionTestUtils.setField(request, "latitude", new BigDecimal("10.555555"));
        ReflectionTestUtils.setField(request, "longitude", new BigDecimal("120.333333"));
        return request;
    }

    private HubUpdateRequestDto hubUpdateRequest(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        HubUpdateRequestDto request = new HubUpdateRequestDto();
        ReflectionTestUtils.setField(request, "name", name);
        ReflectionTestUtils.setField(request, "address", address);
        ReflectionTestUtils.setField(request, "latitude", latitude);
        ReflectionTestUtils.setField(request, "longitude", longitude);
        return request;
    }

    private Hub hubWithId(String name, UUID id) {
        Hub hub = Hub.create(name, "테스트 주소",
                new BigDecimal("37.123456"), new BigDecimal("126.123456"));
        ReflectionTestUtils.setField(hub, "id", id);
        return hub;
    }
}