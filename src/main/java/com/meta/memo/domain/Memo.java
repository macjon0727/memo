package com.meta.memo.domain;

import com.meta.memo.dto.MemoRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name="memo")
@Entity
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //계정 아니고 인덱스임

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "contents", nullable = false, length = 500)
    private String contents;

    public Memo(MemoRequestDto memoRequestDto) {
        this.username = memoRequestDto.getUsername();
        this.contents = memoRequestDto.getContents();
    }

    public void update(MemoRequestDto memoRequestDto){
        this.username = memoRequestDto.getUsername();
        this.contents = memoRequestDto.getContents();
    }
}
