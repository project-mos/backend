package com.mos.backend.studychatrooms.entity;

import com.mos.backend.studies.entity.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_chat_rooms")
public class StudyChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_chat_room_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "study_id")
    @OnDelete(action = CASCADE)
    private Study study;

    public static StudyChatRoom create(Study study) {
        StudyChatRoom studyChatRoom = new StudyChatRoom();
        studyChatRoom.study = study;
        return studyChatRoom;
    }
}
