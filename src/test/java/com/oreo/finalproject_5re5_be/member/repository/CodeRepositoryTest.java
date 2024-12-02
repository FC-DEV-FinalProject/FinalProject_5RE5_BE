package com.oreo.finalproject_5re5_be.member.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.oreo.finalproject_5re5_be.code.entity.Code;
import com.oreo.finalproject_5re5_be.code.repository.CodeRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CodeRepositoryTest {

    @Autowired
    private CodeRepository codeRepository;

    private List<Code> dummy = new ArrayList<>();

    /**
     * 초기화 작업
     * - 자동 주입 확인
     * - 코드 리포지토리 DB 테이블 초기화
     * - 더미 데이터 생성 및 저장
     * - 더미 데이터 정렬
     */

    @BeforeEach
    void setUp() {
        assertNotNull(codeRepository);
        codeRepository.deleteAll();
    }

    /**
     * 코드 리포지토리 테스트 목록
     * - 성공/실패하는 경우에 대해서 테스트 코드 작성
     *
     * 1. 코드 생성
     *    - 성공
     *      1. 코드 생성에 성공함
     *    - 실패
     *      1. 테이블 제약 조건 위배로 코드 생성 실패
     *      2. 코드 번호 중복으로 인한 코드 생성 실패
     *      3. 데이터 입력 형식 오류로 인한 코드 생성 실패
     *
     * 2. 코드 조회
     *    2-1. 특정 코드 번호로 조회
     *       - 성공
     *         1. 특정 코드 번호로 조회시 성공
     *       - 실패
     *         1. 없는 코드 번호로 조회시 조회 실패
     *    2-2. 시퀀스로 조회
     *       - 성공
     *         1. 시퀀스로 조회시 성공
     *       - 실패
     *         1. 없는 시퀀스로 조회시 조회 실패
     *    2-3. 각 파트별 사용 가능한 코드 조회
     *       - 성공
     *         1. 각 파트별 사용 가능한 코드 조회 성공
     *       - 실패
     *         1. 파트 번호가 없는 경우 조회 실패
     *    2-4. 각 파트별 모든 코드 조회
     *       - 성공
     *         1. 각 파트별 모든 코드 조회 성공
     *       - 실패
     *         1. 파트 번호가 없는 경우 조회 실패
     *    2-5. 코드 번호로 코드 존재하는지 확인
     *       - 성공
     *         1. 코드 번호로 코드 존재하는지 확인 성공
     *       - 실패
     *         1. 없는 코드 번호로 코드 존재하는지 확인 실패
     *    2-6. 코드 시퀀스로 코드 존재하는지 확인
     *       - 성공
     *         1. 코드 시퀀스로 코드 존재하는지 확인 성공
     *       - 실패
     *         1. 없는 코드 시퀀스로 코드 존재하는지 확인 실패
     *
     * 3. 코드 수정
     *    - 성공
     *      1. 코드 수정 성공
     *    - 실패
     *      1. 테이블 제약 조건 위배로 수정 실패
     *      2. 코드 번호가 없는 경우 수정 실패
     *      3. 코드 번호 중복으로 인한 수정 실패
     *      4. 데이터 입력 형식 오류로 인한 수정 실패
     *
     * 4. 코드 삭제
     *    - 성공
     *      1. 코드 삭제 성공
     *    - 실패
     *      1. 없는 코드를 삭제하는 경우 삭제 실패
     */


    @DisplayName("코드 생성 - 1. 코드 생성에 성공함")
    @Test
    void 코드_생성_코드_생성에_성공함() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);

        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));
    }

    @DisplayName("코드 생성 - 1. 테이블 제약 조건 위배로 코드 생성 실패")
    @Test
    void 코드_생성_테이블_제약_조건_위배로_코드_생성_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. codeSeq, cateNum, code 는 not null
        // 2. cateNum, code, name은 최대 30자
        // 3. chkUse은 Y, N 만 허용
        // 4. ord는 숫자만 허용


        // cateNum이 null인 DTO를 생성한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다 - DataIntegrityViolationException
        createDummy();
        Code target = dummy.get(0);
        target.setCateNum(null);

        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target));
    }

    @DisplayName("코드 생성 - 2. 코드 번호 중복으로 인한 코드 생성 실패")
    @Test
    void 코드_생성_코드_번호_중복으로_코드_생성_실패() {
        // 제약 조건에는 다음과 같은 것이 있음
        // 1. code는 unique 제약 조건이 있음

        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다

        // 동일한 값의 code로 다른 DTO를 생성한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다

        createDummy();
        Code target1 = dummy.get(0);
        Code target2 = dummy.get(1);

        Code savedCode1 = codeRepository.save(target1);
        assertNotNull(savedCode1);
        assertTrue(isSameCode(target1, savedCode1));

        target2.setCode(target1.getCode());

        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target2));
    }

    @DisplayName("코드 생성 - 3. 데이터 입력 형식 오류로 인한 코드 생성 실패")
    @Test
    void 코드_생성_데이터_입력_형식_오류로_코드_생성_실패() {
        // 제약 조건에는 다음과 같은 것이 있음
        // 1. ord는 숫자만 허용
        // 2. chkUse은 Y, N 만 허용

        // ord에 문자열을 넣은 DTO를 생성한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다
        createDummy();
        Code target = dummy.get(0);
        target.setChkUse("deadwaadw");

        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target));
    }

    @DisplayName("코드 조회 - 2-1. 특정 코드 번호로 조회 성공")
    @Test
    void 코드_조회_특정_코드_번호로_조회_성공() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        // 해당 코드 번호로 조회한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);
        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));

        String code = savedCode.getCode();
        Code foundCode = codeRepository.findCodeByCode(code);

        assertNotNull(foundCode);
        assertTrue(isSameCode(savedCode, foundCode));

    }

    @DisplayName("코드 조회 - 2-1. 없는 코드 번호로 조회 실패")
    @Test
    void 코드_조회_없는_코드_번호로_조회_실패() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        // 없는 코드 번호로 조회한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);

        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));

        String wrongCode = "deadwa";
        Code foundCode = codeRepository.findCodeByCode(wrongCode);
        assertNull(foundCode);
    }

    @DisplayName("코드 조회 - 2-2. 시퀀스로 조회 성공")
    @Test
    void 코드_조회_시퀀스로_조회_성공() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        // 시퀀스로 코드를 조회한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);

        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));

        Long codeSeq = savedCode.getCodeSeq();
        Code foundCode = codeRepository.findCodeByCodeSeq(codeSeq);

        assertNotNull(foundCode);
        assertTrue(isSameCode(savedCode, foundCode));
    }

    @DisplayName("코드 조회 - 2-2. 없는 시퀀스로 조회 실패")
    @Test
    void 코드_조회_없는_시퀀스로_조회_실패() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        // 없는 시퀀스로 조회한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);
        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));

        Long wrongCodeSeq = 100L;
        Code foundCode = codeRepository.findCodeByCodeSeq(wrongCodeSeq);
        assertNull(foundCode);
    }

    @DisplayName("코드 조회 - 2-3. 각 파트별 사용 가능한 코드 조회 성공")
    @Test
    void 코드_조회_각_파트별_사용_가능한_코드_조회_성공() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 여기서 특정 코드만 선별적으로 사용 가능함을 체크한다(예를 들어, chkUse가 Y인 코드만)
        // Y - N - Y
        // 해당 파트의 사용 가능한 코드를 조회한다
        // 더미에서 사용 가능한 코드 더미를 추출한다
        // 정렬을 하여 순서를 보장하고 서로 일치하는지 확인한다

        createDummy();
        String memberPartCateNum = "M";

        List<Code> savedCodes = codeRepository.saveAll(dummy);
        assertNotNull(savedCodes);
        assertEquals(3, savedCodes.size());

        List<Code> expectedCodes = dummy.stream()
                                        .filter(c -> c.getCateNum().equals(memberPartCateNum))
                                        .filter(c -> c.getChkUse().equals("Y"))
                                        .sorted((c1, c2) -> c1.getOrd().compareTo(c2.getOrd()))
                                        .toList();

        List<Code> foundCodes = codeRepository.findAvailableCodesByCateNum(
                memberPartCateNum);

        assertEquals(expectedCodes.size(), foundCodes.size());
        for (int i = 0; i < expectedCodes.size(); i++) {
            assertTrue(isSameCode(expectedCodes.get(i), foundCodes.get(i)));
        }
    }

    @DisplayName("코드 조회 - 2-3. 파트 번호가 없는 경우 조회 실패")
    @Test
    void 코드_조회_파트_번호가_없는_경우_조회_실패() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 여기서 특정 코드만 선별적으로 사용 가능함을 체크한다(예를 들어, chkUse가 Y인 코드만)
        // 존재하지 않는 파트 번호로 사용 가능한 코드를 조회한다
        // 길이가 0인지 확인한다
        createDummy();
        String wrongPartCateNum = "A";

        List<Code> savedCodes = codeRepository.saveAll(dummy);
        assertNotNull(savedCodes);
        assertEquals(3, savedCodes.size());

        List<Code> foundCodes = codeRepository.findAvailableCodesByCateNum(
                wrongPartCateNum);

        assertEquals(0, foundCodes.size());
    }

    @DisplayName("코드 조회 - 2-4. 각 파트별 모든 코드 조회 성공")
    @Test
    void 코드_조회_각_파트별_모든_코드_조회_성공() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 여기서 특정 코드만 선별적으로 사용 가능함을 체크한다(예를 들어, chkUse가 Y인 코드만)
        // Y - N - Y
        // 해당 파트의 모든 코드를 조회한다
        // 정렬을 하여 순서를 보장하고 서로 일치하는지 확인한다
        createDummy();
        String memberPartCateNum = "M";

        List<Code> savedCodes = codeRepository.saveAll(dummy);
        assertNotNull(savedCodes);
        assertEquals(3, savedCodes.size());

        List<Code> expectedCodes = dummy.stream()
                                        .filter(c -> c.getCateNum().equals(memberPartCateNum))
                                        .sorted((c1, c2) -> c1.getOrd().compareTo(c2.getOrd()))
                                        .toList();

        List<Code> foundCodes = codeRepository.findCodesByCateNum(memberPartCateNum);

        assertEquals(expectedCodes.size(), foundCodes.size());
        for (int i = 0; i < expectedCodes.size(); i++) {
            assertTrue(isSameCode(expectedCodes.get(i), foundCodes.get(i)));
        }
    }

    @DisplayName("코드 조회 - 2-4. 파트 번호가 없는 경우 조회 실패")
    @Test
    void 코드_조회_파트_번호가_없는_경우_모든_코드_조회_실패() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 여기서 특정 코드만 선별적으로 사용 가능함을 체크한다(예를 들어, chkUse가 Y인 코드만)
        // 존재하지 않는 파트 번호로 사용 가능한 코드를 조회한다
        // 길이가 0인지 확인한다
        createDummy();
        String wrongPartCateNum = "A";

        List<Code> savedCodes = codeRepository.saveAll(dummy);
        assertNotNull(savedCodes);
        assertEquals(3, savedCodes.size());

        List<Code> foundCodes = codeRepository.findCodesByCateNum(
                wrongPartCateNum);

        assertEquals(0, foundCodes.size());
    }

    @DisplayName("코드 조회 - 2-5. 코드 번호로 코드 존재 하는지 확인 성공")
    @Test
    void 코드_조회_코드_번호로_코드_존재하는지_확인_성공() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 여기서 특정 코드만 선별적으로 사용 가능함을 체크한다(예를 들어, chkUse가 Y인 코드만)
        // 회원 코드 번호로 코드가 존재하는지 확인한다

        createDummy();
        Code target = dummy.get(0);

        codeRepository.saveAll(dummy);

        assertTrue(codeRepository.existsByCode(target.getCode()));
    }

    @DisplayName("코드 조회 - 2-5. 없는 코드 번호로 코드 존재하는지 확인 실패")
    @Test
    void 코드_조회_없는_코드_번호로_코드_존재하는지_확인_실패() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 여기서 특정 코드만 선별적으로 사용 가능함을 체크한다(예를 들어, chkUse가 Y인 코드만)
        // 없는 코드 번호로 코드가 존재하는지 확인한다
        createDummy();
        codeRepository.saveAll(dummy);
        assertFalse(codeRepository.existsByCode("deadwa"));
    }

    @DisplayName("코드 조회 - 2-6. 코드 시퀀스로 코드 존재하는지 확인 성공")
    @Test
    void 코드_조회_코드_시퀀스로_코드_존재하는지_확인_성공() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 해당 더미를 저장한다
        // 이 중에서 하나의 코드를 선택하여 시퀀스로 코드가 존재하는지 확인한다
        createDummy();
        Code target = dummy.get(0);

        codeRepository.saveAll(dummy);

        assertTrue(codeRepository.existsByCodeSeq(target.getCodeSeq()));
    }

    @DisplayName("코드 조회 - 2-6. 없는 코드 시퀀스로 코드 존재하는지 확인 실패")
    @Test
    void 코드_조회_없는_코드_시퀀스로_코드_존재하는지_확인_실패() {
        // 회원 파트에서 사용할 여러 코드를 생성하여 더미에 저장한다
        // 해당 더미를 저장한다
        // 존재하지 않는 시퀀스 번호로 코드가 존재하는지 확인한다
        createDummy();
        codeRepository.saveAll(dummy);
        assertFalse(codeRepository.existsByCodeSeq(100L));
    }

    @DisplayName("코드 수정 - 1. 코드 수정 성공")
    @Test
    void 코드_수정_코드_수정_성공() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);

        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));

        // 코드 DTO를 수정한다
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        String updatedName = "updatedName";
        String updatedChkUse = "N";

        savedCode.setName(updatedName);
        savedCode.setChkUse(updatedChkUse);

        Code updatedCode = codeRepository.save(savedCode);
        assertNotNull(updatedCode);

        assertEquals(updatedName, updatedCode.getName());
        assertEquals(updatedChkUse, updatedCode.getChkUse());
    }

    @DisplayName("코드 수정 - 1. 테이블 제약 조건 위배로 수정 실패")
    @Test
    void 코드_수정_테이블_제약_조건_위배로_수정_실패() {
        // 제약 조건에는 다음과 같은 항목들이 있음
        // 1. codeSeq, cateNum, code 는 not null
        // 2. cateNum, code, name은 최대 30자
        // 3. chkUse은 Y, N 만 허용
        // 4. ord는 숫자만 허용

        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다

        // 코드 DTO를 수정한다. 이때 cateNum을 null로 수정한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다

        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);
        assertNotNull(savedCode);

        target.setCateNum(null);
        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target));
    }


    @DisplayName("코드 수정 - 2. 코드 번호가 없는 경우 수정 실패")
    @Test
    void 코드_수정_코드_번호가_없는_경우_수정_실패() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다

        // 코드 번호가 없는 DTO를 생성한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다

        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);
        assertNotNull(savedCode);

        target.setCode(null);
        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target));
    }

    @DisplayName("코드 수정 - 3. 코드 번호 중복으로 인한 수정 실패")
    @Test
    void 코드_수정_코드_번호_중복으로_수정_실패() {
        // 제약 조건에는 다음과 같은 것이 있음
        // 1. code는 unique 제약 조건이 있음

        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다

        // 동일한 값의 code로 다른 DTO를 생성한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다

        createDummy();
        Code target1 = dummy.get(0);
        Code target2 = dummy.get(1);

        Code savedCode1 = codeRepository.save(target1);
        assertNotNull(savedCode1);

        target2.setCode(savedCode1.getCode());
        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target2));
    }

    @DisplayName("코드 수정 - 4. 데이터 입력 형식 오류로 인한 수정 실패")
    @Test
    void 코드_수정_데이터_입력_형식_오류로_수정_실패() {
        // 제약 조건에는 다음과 같은 것이 있음
        // 1. ord는 숫자만 허용
        // 2. chkUse은 Y, N 만 허용

        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다

        // ord에 문자열을 넣은 DTO를 생성한다
        // Repository 를 통해 저장한다.
        // 이때, 예외 발생 여부를 확인한다

        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);
        assertNotNull(savedCode);

        target.setChkUse("deadwaadw");
        assertThrows(DataIntegrityViolationException.class,
                () -> codeRepository.save(target));

    }

    @DisplayName("코드 삭제 - 1. 코드 삭제 성공")
    @Test
    void 코드_삭제_코드_삭제_성공() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        // 해당 코드를 삭제한다
        // 해당 코드가 삭제됐는지 확인한다
        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);

        assertNotNull(savedCode);
        assertTrue(isSameCode(target, savedCode));

        codeRepository.delete(savedCode);
        assertFalse(codeRepository.existsByCodeSeq(savedCode.getCodeSeq()));
    }

    @DisplayName("코드 삭제 - 1. 없는 코드를 삭제하는 경우 삭제 실패")
    @Test
    void 코드_삭제_없는_코드를_삭제_삭제_실패() {
        // 코드 DTO를 생성한다.
        // Repository 를 통해 저장한다.
        // 해당 코드가 정상적으로 반영됐는지 확인한다
        // 없는 코드를 삭제한다

        createDummy();
        Code target = dummy.get(0);

        Code savedCode = codeRepository.save(target);
        assertNotNull(savedCode);

        codeRepository.delete(savedCode);
        assertFalse(codeRepository.existsByCodeSeq(savedCode.getCodeSeq()));


    }


    private void createDummy() {
        // 더미 데이터 생성
        Code code1 = Code.builder()
                .code("MS001")
                .cateNum("M")
                .name("신규회원")
                .chkUse("Y")
                .ord(1)
                .build();

        Code code2 = Code.builder()
                .code("MS002")
                .cateNum("M")
                .name("VIP회원")
                .chkUse("N")
                .ord(2)
                .build();

        Code code3 = Code.builder()
                .code("MS003")
                .cateNum("M")
                .name("비활성회원")
                .chkUse("Y")
                .ord(3)
                .build();

        dummy.add(code1);
        dummy.add(code2);
        dummy.add(code3);
    }

    private boolean isSameCode(Code expected, Code actual) {
        // 코드 동일한지 비교
        return expected.getCode().equals(actual.getCode())
                && expected.getCateNum().equals(actual.getCateNum())
                && expected.getName().equals(actual.getName())
                && expected.getChkUse().equals(actual.getChkUse())
                && expected.getOrd().equals(actual.getOrd());
    }

}