package com.oreo.finalproject_5re5_be.tts.service;

import com.oreo.finalproject_5re5_be.tts.dto.request.TtsSentenceRequest;
import com.oreo.finalproject_5re5_be.tts.dto.request.TtsSentenceBatchRequest;
import com.oreo.finalproject_5re5_be.tts.dto.response.TtsSentenceDto;
import com.oreo.finalproject_5re5_be.tts.dto.response.TtsSentenceListDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface TtsSentenceService {
    TtsSentenceDto addSentence(@Valid @NotNull Long projectSeq, @Valid TtsSentenceRequest createRequest);
    TtsSentenceDto updateSentence(@Valid @NotNull Long projectSeq, @Valid @NotNull Long tsSeq, @Valid TtsSentenceRequest updateRequest);
    TtsSentenceListDto batchSaveSentence(@Valid @NotNull Long projectSeq, @Valid TtsSentenceBatchRequest batchRequest);
}
