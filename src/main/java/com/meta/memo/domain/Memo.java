package com.meta.memo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Memo {
    private Long id; //계정 아니고 인덱스임
    private String username;
    private String contents;
}
