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
        createDummy();
        codeRepository.saveAll(dummy);
        dummy.sort((c1, c2) -> c1.getOrd().compareTo(c2.getOrd())); // ord 기준으로 정렬
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

    }

    @DisplayName("코드 생성 - 1. 테이블 제약 조건 위배로 코드 생성 실패")
    @Test
    void 코드_생성_테이블_제약_조건_위배로_코드_생성_실패() {

    }

    @DisplayName("코드 생성 - 2. 코드 번호 중복으로 인한 코드 생성 실패")
    @Test
    void 코드_생성_코드_번호_중복으로_코드_생성_실패() {

    }

    @DisplayName("코드 생성 - 3. 데이터 입력 형식 오류로 인한 코드 생성 실패")
    @Test
    void 코드_생성_데이터_입력_형식_오류로_코드_생성_실패() {

    }

    @DisplayName("코드 조회 - 2-1. 특정 코드 번호로 조회 성공")
    @Test
    void 코드_조회_특정_코드_번호로_조회_성공() {

    }

    @DisplayName("코드 조회 - 2-1. 없는 코드 번호로 조회 실패")
    @Test
    void 코드_조회_없는_코드_번호로_조회_실패() {

    }

    @DisplayName("코드 조회 - 2-2. 시퀀스로 조회 성공")
    @Test
    void 코드_조회_시퀀스로_조회_성공() {

    }

    @DisplayName("코드 조회 - 2-2. 없는 시퀀스로 조회 실패")
    @Test
    void 코드_조회_없는_시퀀스로_조회_실패() {

    }

    @DisplayName("코드 조회 - 2-3. 각 파트별 사용 가능한 코드 조회 성공")
    @Test
    void 코드_조회_각_파트별_사용_가능한_코드_조회_성공() {

    }

    @DisplayName("코드 조회 - 2-3. 파트 번호가 없는 경우 조회 실패")
    @Test
    void 코드_조회_파트_번호가_없는_경우_조회_실패() {

    }

    @DisplayName("코드 조회 - 2-4. 각 파트별 모든 코드 조회 성공")
    @Test
    void 코드_조회_각_파트별_모든_코드_조회_성공() {

    }

    @DisplayName("코드 조회 - 2-4. 파트 번호가 없는 경우 조회 실패")
    @Test
    void 코드_조회_파트_번호가_없는_경우_모든_코드_조회_실패() {

    }

    @DisplayName("코드 조회 - 2-5. 코드 번호로 코드 존재하는지 확인 성공")
    @Test
    void 코드_조회_코드_번호로_코드_존재하는지_확인_성공() {

    }

    @DisplayName("코드 조회 - 2-5. 없는 코드 번호로 코드 존재하는지 확인 실패")
    @Test
    void 코드_조회_없는_코드_번호로_코드_존재하는지_확인_실패() {

    }

    @DisplayName("코드 조회 - 2-6. 코드 시퀀스로 코드 존재하는지 확인 성공")
    @Test
    void 코드_조회_코드_시퀀스로_코드_존재하는지_확인_성공() {

    }

    @DisplayName("코드 조회 - 2-6. 없는 코드 시퀀스로 코드 존재하는지 확인 실패")
    @Test
    void 코드_조회_없는_코드_시퀀스로_코드_존재하는지_확인_실패() {

    }

    @DisplayName("코드 수정 - 1. 코드 수정 성공")
    @Test
    void 코드_수정_코드_수정_성공() {

    }

    @DisplayName("코드 수정 - 1. 테이블 제약 조건 위배로 수정 실패")
    @Test
    void 코드_수정_테이블_제약_조건_위배로_수정_실패() {

    }

    @DisplayName("코드 수정 - 2. 코드 번호가 없는 경우 수정 실패")
    @Test
    void 코드_수정_코드_번호가_없는_경우_수정_실패() {

    }

    @DisplayName("코드 수정 - 3. 코드 번호 중복으로 인한 수정 실패")
    @Test
    void 코드_수정_코드_번호_중복으로_수정_실패() {

    }

    @DisplayName("코드 수정 - 4. 데이터 입력 형식 오류로 인한 수정 실패")
    @Test
    void 코드_수정_데이터_입력_형식_오류로_수정_실패() {

    }

    @DisplayName("코드 삭제 - 1. 코드 삭제 성공")
    @Test
    void 코드_삭제_코드_삭제_성공() {

    }

    @DisplayName("코드 삭제 - 1. 없는 코드를 삭제하는 경우 삭제 실패")
    @Test
    void 코드_삭제_없는_코드를_삭제_삭제_실패() {

    }



    @DisplayName("코드 번호로 특정 코드 조회")
    @Test
    void 코드_번호_특정_코드_조회() {
        // 더미 데이터 생성 및 저장
        // 해당 데이터 코드 번호로 조회
        Code foundCode = codeRepository.findCodeByCode("MS001");
        Code expectedCode = dummy.get(0);

        // 결과 동일한지 비교
        assertTrue(isSameCode(expectedCode, foundCode));

    }

    @DisplayName("각 파트별 사용 가능한 코드 조회")
    @Test
    void 각_파트별_사용_가능한_코드_조회() {
        // 회원 파트 여러 더미 데이터 생성 및 저장
        // 해당 파트의 사용 가능한 코드 조회
        List<Code> foundCodes = codeRepository.findAvailableCodesByCateNum("M");
        List<Code> expectedCodes = dummy.stream()
                                        .filter(c -> c.getCateNum().equals("M") && c.getChkUse().equals("Y"))
                                        .toList();

        // 결과 동일한지 비교
        assertTrue(foundCodes.size() == expectedCodes.size());
        for (int i = 0; i < foundCodes.size(); i++) {
            assertTrue(isSameCode(expectedCodes.get(i), foundCodes.get(i)));
        }

    }

    @DisplayName("각 파트별 모든 코드 조회")
    @Test
    void 각_파트별_모든_코드_조회() {
        // 회원 파트 여러 더미 데이터 생성 및 저장
        // 해당 파트의 모든 코드 조회
        List<Code> foundCodes = codeRepository.findCodesByCateNum("M");

        // 결과 동일한지 비교
        assertTrue(foundCodes.size() == dummy.size());


        for (int i = 0; i < foundCodes.size(); i++) {
            assertTrue(isSameCode(dummy.get(i), foundCodes.get(i)));
        }

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