package com.oreo.finalproject_5re5_be.member.dto.request;


import com.oreo.finalproject_5re5_be.member.entity.MemberTermsCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MemberTermConditionUpdateRequest {

    @NotBlank(message = "약관 짧은 내용을 입력해주세요.")
    private String shortCont;

    @NotBlank(message = "약관 긴 내용을 입력해주세요.")
    private String longCont;


    @NotBlank(message = "약관 사용 여부를 입력해주세요.")
    @Pattern(regexp = "^[YN]$", message = "사용 여부는 Y 또는 N만 허용됩니다.")
    private Character chkUse;

    @NotBlank(message = "약관 순서를 입력해주세요.")
    @Pattern(regexp = "^[0-9]{1,3}$", message = "순서는 1~3자의 숫자만 허용됩니다.")
    private Integer ord;
}