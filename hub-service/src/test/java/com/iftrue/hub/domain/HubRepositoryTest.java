package com.iftrue.hub.domain;

import com.iftrue.hub.global.config.AuditorAwareImpl;
import com.iftrue.hub.global.config.JpaAuditingConfig;
import com.iftrue.hub.global.security.AuthenticatedUser;
import com.iftrue.hub.global.security.CurrentUserProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class, CurrentUserProvider.class})
@ActiveProfiles("test")
@DisplayName("[Repository] 허브 레포지토리 테스트")
class HubRepositoryTest {

    @Autowired
    private HubRepository hubRepository;

    @BeforeEach
    void setUpSecurityContext() {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "MASTER");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_MASTER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("삭제되지 않은 허브는 id로 조회된다")
    void findHub() {
        Hub hub = hubRepository.save(hub("조회 테스트 허브"));

        Optional<Hub> found = hubRepository.findByIdAndDeletedAtIsNull(hub.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("조회 테스트 허브");
    }

    @Test
    @DisplayName("soft deleted된 허브는 조회되지 않는다")
    void findHubSoftDeleted() {
        Hub hub = hub("삭제 조회 테스트 허브");
        hub.softDelete(UUID.randomUUID());
        Hub saved = hubRepository.save(hub);

        Optional<Hub> found = hubRepository.findByIdAndDeletedAtIsNull(saved.getId());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("허브 목록 조회 시 soft delete된 허브는 제외된다")
    void findAllActiveHubs() {
        Hub save1 = hubRepository.save(hub("목록1 테스트 허브"));
        Hub save2 = hubRepository.save(hub("목록2 테스트 허브"));
        Hub deleted = hub("목록 삭제 테스트 허브");
        deleted.softDelete(UUID.randomUUID());
        Hub deletedSaved = hubRepository.save(deleted);

        Page<Hub> page = hubRepository.findAllByDeletedAtIsNull(PageRequest.of(0, 100));

        assertThat(page.getContent()).extracting(Hub::getId)
                .contains(save1.getId(), save2.getId())
                .doesNotContain(deletedSaved.getId());
    }

    @Test
    @DisplayName("soft deleted된 허브를 제외하고, 키워드로 이름이 부분 검색 된다")
    void searchByKeyword() {
        Hub seoul = hubRepository.save(hub("검색 대상 테스트 허브"));
        Hub busan = hubRepository.save(hub("검색 제외 테스트 허브"));
        Hub deleted = hub("검색 대상 삭제 테스트 허브");
        deleted.softDelete(UUID.randomUUID());
        Hub deletedSaved = hubRepository.save(deleted);

        Page<Hub> page = hubRepository.search("검색 대상", PageRequest.of(0, 100));

        assertThat(page.getContent()).extracting(Hub::getId)
                .contains(seoul.getId())
                .doesNotContain(busan.getId(), deletedSaved.getId());
    }

    @Test
    @DisplayName("soft delete되지 않은 허브 이름 중복 저장 시 유니크 인덱스 예외가 발생한다")
    void rejectDuplicateActiveName() {
        hubRepository.saveAndFlush(hub("중복 방지 테스트 허브"));

        assertThatThrownBy(() ->
                hubRepository.saveAndFlush(hub("중복 방지 테스트 허브")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("soft delete된 허브 이름은 다시 등록할 수 있다")
    void allowReuseNameAfterSoftDelete() {
        Hub first = hubRepository.saveAndFlush(hub("재등록 테스트 허브"));
        first.softDelete(UUID.randomUUID());
        hubRepository.saveAndFlush(first);

        assertThatCode(() ->
                hubRepository.saveAndFlush(hub("재등록 테스트 허브")))
                .doesNotThrowAnyException();
    }

    private Hub hub(String name) {
        return Hub.create(
                name,
                "테스트 주소",
                new BigDecimal("37.563600"),
                new BigDecimal("126.982500"));
    }
}
