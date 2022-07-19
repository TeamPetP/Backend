package petPeople.pet.util;

import lombok.extern.slf4j.Slf4j;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RequestUtil {

    // 헤더값 검증
    public static String getAuthorizationToken(String header) {

        //헤더값에 Authorization 값이 없거나 유효하지 않은 경우
        if(header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_AUTHORIZATION, "**** Authorization Header is Null or Empty ******");
        }

        // parts[0] : bearer, parts[1] : token
        String[] parts = header.split(" ");
        if(parts.length != 2) {
            throw new CustomException(ErrorCode.INVALID_AUTHORIZATION);
        }

        //Token return
        return parts[1];
    }

    public static String getAuthorizationToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization == "") return null;
        return getAuthorizationToken(authorization);
    }
}
