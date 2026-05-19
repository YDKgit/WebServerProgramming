package com.example.springstudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "공통 API 응답 래퍼")
public class CommonResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    @Schema(description = "응답 데이터")
    private T data;

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(true, data);
    }
}
