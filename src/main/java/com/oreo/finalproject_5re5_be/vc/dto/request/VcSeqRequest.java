package com.oreo.finalproject_5re5_be.vc.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class VcSeqRequest {
    private Long seq1;
    private Long seq2;
}
