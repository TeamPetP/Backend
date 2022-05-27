package petPeople.pet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    //공통 예외
    BAD_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    BAD_REQUEST_VALIDATION(HttpStatus.BAD_REQUEST, "검증에 실패하였습니다."),

    //회원 예외
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "해당 요청은 로그인이 필요합니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    EXIST_MEMBER(HttpStatus.BAD_REQUEST, "이미 등록된 유저입니다."),

    //인증 인가 예외
    FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "해당 요청에 권한이 없습니다."),
    INVALID_AUTHORIZATION(HttpStatus.BAD_REQUEST, "인증 정보가 부정확합니다."),

    //북마크 예외
    BOOKMARKED_POST(HttpStatus.BAD_REQUEST, "이미 북마크한 피드입니다."),
    NEVER_BOOKMARKED_POST(HttpStatus.BAD_REQUEST, "북마크하지 않은 피드입니다."),

    //리뷰 예외
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."),

    //알림 예외
    NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),

    //게시글 예외
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),

    //모임 예외
    NOT_FOUND_MEETING(HttpStatus.NOT_FOUND, "해당 모임을 찾을 수 없습니다."),
    FULL_MEMBER_MEETING(HttpStatus.BAD_REQUEST, "해당 모임에 인원이 다 찼습니다."),
    EXPIRED_MEETING(HttpStatus.BAD_REQUEST, "모집이 마감되었습니다."),
    DUPLICATED_JOIN_MEETING(HttpStatus.BAD_REQUEST, "이미 가입한 모임입니다."),
    NOT_VALID_AGE(HttpStatus.BAD_REQUEST, "가입조건에 해당하지 않는 나이입니다."),

    //댓글 예외
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String detail;
}