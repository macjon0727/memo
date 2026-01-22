package com.meta.memo.service;

import com.meta.memo.domain.Memo;
import com.meta.memo.dto.MemoRequestDto;
import com.meta.memo.dto.MemoResponseDto;
import com.meta.memo.repository.MemoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemoService {
    // 멤버 변수 선언
    private final MemoRepository memoRepository;
    // 생성자 주입(DI)
    public MemoService(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    @Transactional
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto memoRequestDto) {
        // RequestDto -> Entity 변환
        Memo newMemo = new Memo(memoRequestDto);
        Memo savedMemo = memoRepository.save(newMemo);
        // Entity -> ResponseDto 변환
        MemoResponseDto memoResponseDto = new MemoResponseDto(savedMemo);
        return memoResponseDto;
    }

    public List<MemoResponseDto> getMemos() {
        return memoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(MemoResponseDto::new)
                .toList();
    }

    public List<MemoResponseDto> getMemosByKeyword(String keyword) {
        // 1. 리포지토리에 새로 만든 쿼리 메서드를 호출하여 검색 결과(Entity 리스트)를 가져옵니다.
        // 2. Stream API를 사용하여 Entity 객체들을 MemoResponseDto로 변환합니다.
        // 3. 최종적으로 List 형태로 반환합니다.
        return memoRepository.findAllByContentsContainingOrderByModifiedAtDesc(keyword).stream()
                .map(MemoResponseDto::new)
                .toList();
    }

    public Memo getMemoById(Long id) {
        return memoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 id의 메모는 존재하지 않습니다."));
    }

    @Transactional
    public Long updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto memoRequestDto) {
        // 해당 id의 메모리가 데이터베이스에 존재하는지 확인
        Memo foundMemo = getMemoById(id);
        //메모 내용 수정
        foundMemo.update(memoRequestDto);
        return id;
    }

    @Transactional
    public Long deleteMemo(@PathVariable Long id) {
        //해당 id의 메모가 존재하는지 확인
        Memo foundMemo = getMemoById(id);
        //메모 내용 삭제
        memoRepository.delete(foundMemo);
        return id;
    }


}
