package com.oreo.finalproject_5re5_be.concat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.sound.sampled.AudioFormat;

@Repository
public interface AudioFormatRepository extends JpaRepository<AudioFormat, Long> {
}
