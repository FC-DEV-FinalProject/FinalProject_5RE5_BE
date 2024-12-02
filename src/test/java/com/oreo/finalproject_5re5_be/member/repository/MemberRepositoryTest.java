package com.oreo.finalproject_5re5_be.member.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.oreo.finalproject_5re5_be.code.entity.Code;
import com.oreo.finalproject_5re5_be.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private List<Member> dummy = new ArrayList<>();

    /**
     * 초기화 작업
     * - 자동 주입 확인
     * - 코드 리포지토리 DB 테이블 초기화
     */
    @BeforeEach
    void setUp() {
        assertNotNull(memberRepository);
        memberRepository.deleteAll();
        dummy.clear();
    }


    /**
     * 회원 리포지토리 테스트 목록
     * - 성공/실패하는 경우에 대해서 테스트 코드 작성
     *
     * 1. 회원 생성
     *    - 성공
     *      1. 회원 생성에 성공함
     *    - 실패
     *      1. 테이블 제약 조건 위배로 회원 생성 실패
     *      2. 회원 아이디 중복으로 인한 회원 생성 실패
     *      3. 회원 이메일 중복으로 인한 회원 생성 실패
     *      4. 데이터 입력 형식 오류로 인한 회원 생성 실패
     *
     * 2. 회원 조회
     *    2-1. 시퀀스로 조회
     *       - 성공
     *       1. 시퀀스로 조회 성공
     *
     *       - 실패
     *       1. 없는 시퀀스로 조회하는 경우 조회 실패
     *
     *    2-2. 이메일로 조회
     *       - 성공
     *       1. 이메일로 조회 성공
     *
     *       - 실패
     *       1. 없는 이메일로 조회하는 경우 조회 실패
     *
     *    2-3. 아이디로 조회
     *       - 성공
     *       1. 아이디로 조회 성공
     *
     *       - 실패
     *       1. 없는 아이디로 조회하는 경우 조회 실패
     *
     * 3. 회원 수정
     *    - 성공
     *      1. 회원 수정 성공
     *
     *    - 실패
     *      1. 테이블 제약 조건 위배로 수정 실패
     *      2. 회원 아이디 중복으로 인한 수정 실패
     *      3. 회원 이메일 중복으로 인한 수정 실패
     *      4. 데이터 입력 형식 오류로 인한 수정 실패
     *
     * 4. 회원 삭제
     *    - 성공
     *      1. 회원 삭제 성공
     *    - 실패
     *      1. 없는 회원을 삭제하는 경우 삭제 실패
     */

    @Test
    @DisplayName("1. 회원 생성 - 1. 회원 생성에 성공함")
    void 회원_생성_성공() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        createDummy();
        Member target = dummy.get(0);

        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));
    }

    @Test
    @DisplayName("1. 회원 생성 - 1. 테이블 제약 조건 위배로 회원 생성 실패")
    void 회원_생성_테이블_제약조건_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, password, name, chkValid 는 not-null
        // 2. id, email 은 unique
        // 3. chkValid 는 Y, N 만 허용

        // 회원 DTO를 생성한다
        // DTO의 id를 null로 설정한다
        // 예외 발생 여부를 확인한다
        createDummy();
        Member target = dummy.get(0);
        target.setId(null);

        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(target));
    }

    @Test
    @DisplayName("1. 회원 생성 - 2. 회원 아이디 중복으로 인한 회원 생성 실패")
    void 회원_생성_아이디_중복_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, password, name, chkValid 는 not-null
        // 2. id, email 은 unique
        // 3. chkValid 는 Y, N 만 허용

        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        createDummy();
        Member member1 = dummy.get(0);

        Member savedMember = memberRepository.save(member1);
        assertNotNull(savedMember);
        assertTrue(isSameMember(member1, savedMember));

        // 회원 DTO를 생성한다
        // DTO의 id를 저장된 회원의 id로 설정한다
        // 예외 발생 여부를 확인한다

        Member member2 = dummy.get(1);

        member2.setId(savedMember.getId());
        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(member2));
    }

    @Test
    @DisplayName("1. 회원 생성 - 3. 회원 이메일 중복으로 인한 회원 생성 실패")
    void 회원_생성_이메일_중복_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, password, name, chkValid 는 not-null
        // 2. id, email 은 unique
        // 3. chkValid 는 Y, N 만 허용

        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        createDummy();
        Member member1 = dummy.get(0);

        Member savedMember = memberRepository.save(member1);
        assertNotNull(savedMember);
        assertTrue(isSameMember(member1, savedMember));

        // 회원 DTO를 생성한다
        // DTO의 email를 저장된 회원의 email로 설정한다
        // 예외 발생 여부를 확인한다
        Member member2 = dummy.get(1);

        member2.setEmail(savedMember.getEmail());
        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(member2));
    }

    @Test
    @DisplayName("1. 회원 생성 - 4. 데이터 입력 형식 오류로 인한 회원 생성 실패")
    void 회원_생성_형식오류_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, name의 길이 제한이 있음. 각각 30, 50, 30

        // 회원 DTO를 생성한다
        // DTO의 id를 50자로 설정한다
        // 예외 발생 여부를 확인한다
        createDummy();
        Member target = dummy.get(0);
        target.setId("a".repeat(50)); // aaa ... aaa

        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(target));
    }

    @Test
    @DisplayName("2. 회원 조회 - 2-1. 시퀀스로 조회 - 1. 시퀀스로 조회 성공")
    void 회원_조회_시퀀스_성공() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        // 조회된 회원의 시퀀스를 이용하여 다시 조회한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        Long seq = savedMember.getSeq();
        Member foundMember = memberRepository.findBySeq(seq);
        assertNotNull(foundMember);
        assertTrue(isSameMember(savedMember, foundMember));
    }

    @Test
    @DisplayName("2. 회원 조회 - 2-1. 시퀀스로 조회 - 1. 없는 시퀀스로 조회하는 경우 조회 실패")
    void 회원_조회_시퀀스_실패() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        // 존재하지 않는 시퀀스로 조회하여 조회 실패한다

        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        Long wrongSeq = 11111L;
        Member foundMember = memberRepository.findBySeq(wrongSeq);
        assertNull(foundMember);

    }

    @Test
    @DisplayName("2. 회원 조회 - 2-2. 이메일로 조회 - 1. 이메일로 조회 성공")
    void 회원_조회_이메일_성공() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        // 등록된 이메일로 조회하여 조회 성공한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        String email = savedMember.getEmail();
        Member foundMember = memberRepository.findByEmail(email);
        assertNotNull(foundMember);
        assertTrue(isSameMember(savedMember, foundMember));
    }

    @Test
    @DisplayName("2. 회원 조회 - 2-2. 이메일로 조회 - 1. 없는 이메일로 조회하는 경우 조회 실패")
    void 회원_조회_이메일_실패() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        // 존재하지 않는 이메일로 조회하여 조회 실패한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        String wrongEmail = "dwadwa@emske.com";
        Member foundMember = memberRepository.findByEmail(wrongEmail);
        assertNull(foundMember);
    }

    @Test
    @DisplayName("2. 회원 조회 - 2-3. 아이디로 조회 - 1. 아이디로 조회 성공")
    void 회원_조회_아이디_성공() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        // 등록된 아이디로 조회하여 조회 성공한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        String id = savedMember.getId();
        Member foundMember = memberRepository.findById(id);
        assertNotNull(foundMember);
        assertTrue(isSameMember(savedMember, foundMember));
    }

    @Test
    @DisplayName("2. 회원 조회 - 2-3. 아이디로 조회 - 1. 없는 아이디로 조회하는 경우 조회 실패")
    void 회원_조회_아이디_실패() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다
        // 존재하지 않는 아이디로 조회하여 조회 실패한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        String wrongId = "dwa21321@1";
        Member foundMember = memberRepository.findById(wrongId);
        assertNull(foundMember);
    }

    @Test
    @DisplayName("3. 회원 수정 - 1. 회원 수정 성공")
    void 회원_수정_성공() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다

        // 저장된 회원의 정보를 수정한다
        // 수정된 회원을 저장한다
        // 수정된 회원을 조회한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        String newId = "new qwer1234";
        savedMember.setId(newId);
        Member updatedMember = memberRepository.save(savedMember);

        assertNotNull(updatedMember);
        assertEquals(newId, updatedMember.getId());

    }

    @Test
    @DisplayName("3. 회원 수정 - 1. 테이블 제약 조건 위배로 수정 실패")
    void 회원_수정_제약조건_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, password, name, chkValid 는 not-null
        // 2. id, email 은 unique
        // 3. chkValid 는 Y, N 만 허용

        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다

        // 저장된 회원의 정보를 수정한다
        // 수정된 회원의 id를 null로 설정한다
        // 예외 발생 여부를 확인한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        savedMember.setId(null);
        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(savedMember));
    }

    @Test
    @DisplayName("3. 회원 수정 - 2. 회원 아이디 중복으로 인한 수정 실패")
    void 회원_수정_아이디_중복_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, password, name, chkValid 는 not-null
        // 2. id, email 은 unique
        // 3. chkValid 는 Y, N 만 허용

        // 2개의 회원 DTO를 생성한다
        // Repository를 통해 2명의 회원을 저장한다
        // 저장된 회원을 조회한다

        // 한 회원의 아이디를 다른 회원의 아이디로 변경한다
        // 예외 발생 여부를 확인한다
        createDummy();
        Member member1 = dummy.get(0);
        Member member2 = dummy.get(1);

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        assertNotNull(savedMember1);
        assertNotNull(savedMember2);

        savedMember2.setId(savedMember1.getId());
        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(savedMember2));
    }

    @Test
    @DisplayName("3. 회원 수정 - 3. 회원 이메일 중복으로 인한 수정 실패")
    void 회원_수정_이메일_중복_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, password, name, chkValid 는 not-null
        // 2. id, email 은 unique
        // 3. chkValid 는 Y, N 만 허용

        // 2개의 회원 DTO를 생성한다
        // Repository를 통해 2명의 회원을 저장한다
        // 저장된 회원을 조회한다

        // 한 회원의 이메일을 다른 회원의 이메일로 변경한다
        // 예외 발생 여부를 확인한다
        createDummy();

        Member member1 = dummy.get(0);
        Member member2 = dummy.get(1);

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        assertNotNull(savedMember1);
        assertNotNull(savedMember2);

        savedMember2.setEmail(savedMember1.getEmail());

        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(savedMember2));
    }

    @Test
    @DisplayName("3. 회원 수정 - 4. 데이터 입력 형식 오류로 인한 수정 실패")
    void 회원_수정_형식오류_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. id, email, name의 길이 제한이 있음. 각각 30, 50, 30

        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다

        // 저장된 회원의 정보를 수정한다
        // 수정된 회원의 id를 50자로 설정한다
        // 예외 발생 여부를 확인한다

        createDummy();
        Member target = dummy.get(0);

        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);

        String newId = "a".repeat(50); // aaa ... aaa
        savedMember.setId(newId);

        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(savedMember));
    }

    @Test
    @DisplayName("4. 회원 삭제 - 1. 회원 삭제 성공")
    void 회원_삭제_성공() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다

        // 저장된 회원을 삭제한다
        // 삭제된 회원을 조회한다
        createDummy();
        Member target = dummy.get(0);
        Member savedMember = memberRepository.save(target);
        assertNotNull(savedMember);
        assertTrue(isSameMember(target, savedMember));

        memberRepository.delete(savedMember);
        Member foundMember = memberRepository.findBySeq(savedMember.getSeq());
        assertNull(foundMember);
    }

    @Test
    @DisplayName("4. 회원 삭제 - 1. 없는 회원을 삭제하는 경우 삭제 실패")
    void 회원_삭제_실패() {
        // 회원 DTO를 생성한다
        // Repository를 통해 회원을 저장한다
        // 저장된 회원을 조회한다

        // 존재하지 않는 회원을 삭제한다
        // 예외 발생 여부를 확인한다
    }

    @Test
    @DisplayName("변경된 이메일이 다른 회원의 이메일과 중복되는 경우 true 반환")
    void 변경된_이메일_다른_회원_이메일_중복되는_경우_true_반환() {
        // 더미 회원 생성
        createDummy();
        Member member1 = dummy.get(0);
        Member member2 = dummy.get(1);

        // 더미 회원 등록
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        assertNotNull(savedMember1);
        assertNotNull(savedMember2);

        // 등록된 두 더미 회원을 조회함
        Member foundMember1 = memberRepository.findById(member1.getSeq()).get();
        Member foundMember2 = memberRepository.findById(member2.getSeq()).get();
        assertNotNull(foundMember1);
        assertNotNull(foundMember2);

        // 한 회원의 이메일을 다른 회원의 이메일로 변경함
        // 그때 repository 의 existsByEmailNotContainingMemberSeq 메서드를 이용하여 중복 유무 확인
        boolean isDuplicated = memberRepository.existsByEmailNotContainingMemberSeq(foundMember1.getSeq(), foundMember2.getEmail());
        assertTrue(isDuplicated);
    }

    @Test
    @DisplayName("변경된 아이디가 다른 회원의 아이디와 중복되는 경우 true 반환")
    void 변경된_아이디_다른_회원_아이디_중복되는_경우_true_반환() {
        // 더미 회원 생성
        createDummy();
        Member member1 = dummy.get(0);
        Member member2 = dummy.get(1);

        // 더미 회원 등록
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        assertNotNull(savedMember1);
        assertNotNull(savedMember2);

        // 등록된 두 더미 회원을 조회함
        Member foundMember1 = memberRepository.findById(member1.getSeq()).get();
        Member foundMember2 = memberRepository.findById(member2.getSeq()).get();

        assertNotNull(foundMember1);
        assertNotNull(foundMember2);
        // 한 회원의 아이디를 다른 회원의 아이디로 변경함
        // 그때 repository 의 existsByEmailNotContainingMemberSeq 메서드를 이용하여 중복 유무 확인
        boolean isDuplicated = memberRepository.existsByIdNotContainingMemberSeq(foundMember1.getSeq(), foundMember2.getId());
        assertTrue(isDuplicated);
    }



    private void createDummy() {
        Member member1 = Member.builder()
                        .id("qwerfde2312")
                        .password("asdf12341234@")
                        .email("qwefghnm1212@gmail.com")
                        .name("홍길동")
                        .normAddr("서울시 강남구")
                        .locaAddr("서울시")
                        .detailAddr("서초동 123-456")
                        .passAddr("서초대로 59-32")
                        .chkValid('Y')
                        .build();

        Member member2 = Member.builder()
                        .id("qwerfdwadwad2312")
                        .password("asdf12341234@dwa")
                        .email("wdwadaw2323@naver.com")
                        .name("홍만동")
                        .normAddr("서울시 강남구")
                        .locaAddr("서울시")
                        .detailAddr("서초동 123-456")
                        .passAddr("서초대로 59-32")
                        .chkValid('Y')
                        .build();

        dummy.add(member1);
        dummy.add(member2);
    }

    private boolean isSameMember(Member member1, Member member2) {
        return member1.getId().equals(member2.getId())
                && member1.getEmail().equals(member2.getEmail())
                && member1.getName().equals(member2.getName())
                && member1.getNormAddr().equals(member2.getNormAddr())
                && member1.getLocaAddr().equals(member2.getLocaAddr())
                && member1.getDetailAddr().equals(member2.getDetailAddr())
                && member1.getPassAddr().equals(member2.getPassAddr())
                && member1.getChkValid().equals(member2.getChkValid());
    }
}