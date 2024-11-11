package com.oreo.finalproject_5re5_be.vc.service;


import com.oreo.finalproject_5re5_be.vc.dto.request.*;
import com.oreo.finalproject_5re5_be.vc.dto.response.VcResponse;

import java.util.List;

public interface VcService {
    /*
        저장
        1. SRC 파일 저장 (Long ProjectSeq, integer rowOrder,String name, String fileUrl,
         String length,String size,String extension) (VcSrcRequest)
        2. TRG 파일 저장 (Long SrcSeq, String name, String fileUrl,
         Long length, String size, String extension) (VcAudioRequest)
        3. Result 파일 저장 (Long SrcSeq, String name, String fileUrl,
         Long length, String size, String extension) (VcAudioRequest)
        4. 텍스트 저장 (SrcSeq, text) (VcTextRequest)

        조회
        5. 프로젝트 행 전체 조회 (ProjectSeq)(VCResponse)

        6. SRC 파일 다운로드 (SrcSeq)(String)
        7. SRC 파일 재생 (SrcSeq)(String)

        8. Result 파일 다운로드 (ResultSeq)(String)
        9. Result 파일 재생 (ResultSeq)(String)

        수정
        10. 행 텍스트 수정 (TextSeq, text)
        11. 행 순서 변경 (SrcSeq, rowOrder)
     */
    void SrcSave(VcSrcRequest vcSrcRequest);
    void TrgSave(VcAudioRequest vcAudioRequest);
    void ResultSave(VcAudioRequest vcAudioRequest);
    void TextSave(VcTextRequest vcTextRequest);

    List<VcResponse> getVcResponse(Long ProjectSeq);
    String getSrcFile(Long seq);
    String getTrgFile(Long seq);

    void updateText(Long seq, String text);
    void updateRowOrder(Long seq, int rowOrder);
}