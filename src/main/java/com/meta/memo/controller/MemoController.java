package com.meta.memo.controller;

import com.meta.memo.domain.Memo;
import com.meta.memo.dto.MemoRequestDto;
import com.meta.memo.dto.MemoResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/memos")
public class MemoController {
    //JDBC를 통한 MySQL 연결
    private final JdbcTemplate jdbcTemplate;

    public MemoController(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping()
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto memoRequestDto) {
        // RequestDto -> Entity 로 변환
        Memo newMemo = new Memo(memoRequestDto);

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); //기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO memo (username, contents) VALUES (?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,newMemo.getUsername());
            preparedStatement.setString(2,newMemo.getContents());
            return preparedStatement;
        }, keyHolder);

        // DB INSERT 후 받아온 키 확인
        Long id = keyHolder.getKey().longValue();
        newMemo.setId(id);


        // Entity -> ResponseDto 변환
        MemoResponseDto memoResponseDto = new MemoResponseDto(newMemo);

        return memoResponseDto;
    }
//
//    @GetMapping()
//    public List<MemoResponseDto> getMemos() {
//        // (임시) Map -> List
//        List<MemoResponseDto> memoResponseDtoList = memoList.values().stream()
//                .map(MemoResponseDto::new).toList();
//
//        return memoResponseDtoList;
//    }
//
//    @PutMapping("{id}")
//    public Long updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto memoRequestDto) {
//        // 해당 id의 메모리가 데이터베이스에 존재하는지 확인
//        if (memoList.containsKey(id)) {
//            // true면, 해당 메모 가져오기
//            Memo foundMemo = memoList.get(id);
//            foundMemo.update(memoRequestDto);
//
//            return foundMemo.getId();
//        } else {
//            throw new IllegalArgumentException("선택한 id의 메모는 존재하지 않습니다.");
//        }
//    }
//
//    @DeleteMapping("{id}")
//    public Long deleteMemo(@PathVariable Long id) {
//        // 해당 id의 메모리가 데이터베이스에 존재하는지 확인
//        if (memoList.containsKey(id)) {
//            // true 면, 해당 메모 삭제
//            memoList.remove(id);
//            return id;
//        }else {
//            throw new IllegalArgumentException("선택한 id의 메모는 존재하지 않습니다.");
//        }
//    }
}
