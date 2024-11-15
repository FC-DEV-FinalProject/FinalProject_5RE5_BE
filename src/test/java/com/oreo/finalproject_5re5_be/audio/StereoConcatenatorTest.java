package com.oreo.finalproject_5re5_be.audio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StereoConcatenatorTest {

    public static final Concatenator concatenator = new StereoConcatenator();

    @Test
    @DisplayName("병합한 오디오가 스테레오 포맷이다.")
    void concatenateTest() throws UnsupportedAudioFileException, IOException {
        AudioResample audioResample = new AudioResample(AudioFormats.STEREO_FORMAT);// 스테레오 포맷
        // 오디오 파일 불러오기
        File inputFile = new File("test.mp3");
        byte[] bytes = AudioExtensionConverter.mp3ToWav(inputFile);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        AudioInputStream inputAIS = AudioSystem.getAudioInputStream(byteArrayInputStream);


        File inputFile2 = new File("ttsoutput.mp3");
        byte[] bytes2 = AudioExtensionConverter.mp3ToWav(inputFile2);
        ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(bytes2);
        AudioInputStream inputAIS2 = AudioSystem.getAudioInputStream(byteArrayInputStream2);

        //병합
        ByteArrayOutputStream concatenate = concatenator
                .concatenate(audioResample.resample(List.of(inputAIS, inputAIS2)));

        //병합된 오디오를 리샘플링
        AudioInputStream resample = audioResample.resample(concatenate);

        //변환 한 오디오 포맷 확인
        assertThat(resample.getFormat()).isEqualTo(AudioFormats.STEREO_FORMAT);
    }

    @Test
    @DisplayName("스테레오 포맷 병합에서 모노 타입을 넣으면 null을 반환한다.")
    void concatenateNotStereoTest() throws UnsupportedAudioFileException, IOException {
        AudioResample audioResample = new AudioResample(AudioFormats.MONO_FORMAT);

        // 오디오 파일 불러오기
        File inputFile = new File("test.mp3");
        byte[] bytes = AudioExtensionConverter.mp3ToWav(inputFile);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        AudioInputStream inputAIS = AudioSystem.getAudioInputStream(byteArrayInputStream);


        File inputFile2 = new File("ttsoutput.mp3");
        byte[] bytes2 = AudioExtensionConverter.mp3ToWav(inputFile2);
        ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(bytes2);
        AudioInputStream inputAIS2 = AudioSystem.getAudioInputStream(byteArrayInputStream2);

        //병합
        ByteArrayOutputStream concatenate = concatenator
                .concatenate(audioResample.resample(List.of(inputAIS, inputAIS2)));
        //변환 한 오디오 포맷 확인
        assertThat(concatenate).isNull();

    }
}