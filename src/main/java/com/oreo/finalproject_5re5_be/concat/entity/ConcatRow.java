package com.oreo.finalproject_5re5_be.concat.entity;

import com.oreo.finalproject_5re5_be.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "concat_row")
public class ConcatRow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concet_row_seq")
    private Long concatRowSeq;

    @ManyToOne
    @JoinColumn(name = "pro_seq")
    private ConcatTab concatTab;

    @OneToOne(mappedBy = "concatRow", cascade = CascadeType.ALL)
    private AudioFile audioFile;

    private String rowText;
    private Character selected;
    private Float silence;
    private Integer rowIndex;
    private Character status;


}
