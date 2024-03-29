package com.sparta.market.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATED_EMAIL("DUPLICATED_EMAIL", "중복된 이메일입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_USER("NOT_EXIST_USER", "해당 유저는 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_PWD("NOT_MATCH_PWD", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    AUTHORITY_ACCESS("AUTHORITY_ACCESS", "접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    VALIDATION_ERROR("VALIDATION_ERROR", "잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다. ADMIN에게 문의하세요.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("UNAUTHORIZED", "로그인 후 이용할 수 있습니다. 계정이 없다면 회원 가입을 진행해주세요.", HttpStatus.UNAUTHORIZED),
    NOT_EXIST_COMMENT("NOT_EXIST_COMMENT", "해당 댓글은 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_POST("NOT_EXIST_POST", "해당 글은 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_YOUR_POST("NOT_YOUR_POST","해당 게시글을 작성한 유저가 아닙니다." ,HttpStatus.BAD_REQUEST),
    NOT_YOUR_IMG("NOT_YOUR_IMG", "imgId를 확인해주세요", HttpStatus.BAD_REQUEST),
    NOT_EXIST_IMG("NOT_EXIST_IMG", "해당 사진이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    FAIL_TO_SEND_MAIL("FAIL_TO_SEND_MAIL", "메일을 보내는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATED_PHONE_NUMBER("DUPLICATED_PHONE_NUMBER", "중복된 전화번호입니다.", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_INPUT("INVALID_CATEGORY_INPUT", "잘못된 카테고리 입력입니다.", HttpStatus.BAD_REQUEST),
    NOT_YOUR_COMMENT("NOT_YOUR_COMMENT", "해당 댓글을 작성한 유저가 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_CODE("NOT_MATCH_CODE", "인증번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),
    MSG_TIME_OUT("MSG_TIME_OUT", "인증시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT),
    NOT_EXIST_PROFILE("NOT_EXIST_PROFILE", "프로필사진이 없습니다", HttpStatus.BAD_REQUEST),
    INVALID_PARENT_COMMENT("INVALID_PARENT_COMMENT", "유효하지 않은 부모 댓글입니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_NOT_FOUND("CHATROOM_NOT_FOUND", "채팅방을 찾을 수 없습니다", HttpStatus.NOT_FOUND);


    private final String key;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String key, String message, HttpStatus httpStatus) {
        this.key = key;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

