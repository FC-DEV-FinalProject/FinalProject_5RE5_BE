package com.oreo.finalproject_5re5_be.concat.entity;

import com.oreo.finalproject_5re5_be.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "audio_format")
public class AudioFormat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audio_format_seq")
    private Long audioFormatSeq;


    @OneToOne(mappedBy = "audio_format")
    private AudioFile audioFile;

    @OneToOne(mappedBy = "audio_format")
    private ConcatResult concatResult;


    private String encoding;
    private Integer sampleRate;
    private Short sampleSizeBit;
    private Short channel;
    private Integer frameSize;
    private Short frameRate;
    private Character isBigEndian;

}
