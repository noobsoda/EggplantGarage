package com.ssafy.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.api.request.UserLoginPostReq;
import com.ssafy.api.response.UserLoginPostRes;
import com.ssafy.api.service.UserService;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.common.util.JwtTokenUtil;
import com.ssafy.db.entity.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 인증 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "인증 API", tags = {"Auth."})
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "<strong>아이디와 패스워드</strong>를 통해 로그인 한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = UserLoginPostRes.class),
            @ApiResponse(code = 401, message = "인증 실패", response = BaseResponseBody.class),
            @ApiResponse(code = 404, message = "사용자 없음", response = BaseResponseBody.class),
            @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)
    })
    public ResponseEntity<UserLoginPostRes> login(@RequestBody @ApiParam(value = "로그인 정보", required = true) UserLoginPostReq loginInfo, HttpServletResponse response) {
        String userEmail = loginInfo.getEmail();
        String password = loginInfo.getPassword();

        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(404).body(UserLoginPostRes.of(404, "Not Exist", null));
        }
        // 로그인 요청한 유저로부터 입력된 패스워드 와 디비에 저장된 유저의 암호화된 패스워드가 같은지 확인.(유효한 패스워드인지 여부 확인)
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 유효한 패스워드가 맞는 경우, 로그인 성공으로 응답.(액세스 토큰을 포함하여 응답값 전달)
            String refreshToken = JwtTokenUtil.getRefreshToken(userEmail);
            userService.patchUserTokenByrefreshToken(userEmail, refreshToken);

            Cookie cookie = new Cookie("refreshToken", refreshToken); // refresh 담긴 쿠키 생성
            cookie.setMaxAge(JwtTokenUtil.refreshExpirationTime); // 쿠키의 유효시간을 refresh 유효시간만큼 설정
            cookie.setSecure(true); // 클라이언트가 HTTPS가 아닌 통신에서는 해당 쿠키를 전송하지 않도록 하는 설정
            cookie.setHttpOnly(true); // 브라우저에서 쿠키에 접근할 수 없도록 하는 설정
            cookie.setPath("/");

            response.addCookie(cookie);

            return ResponseEntity.ok(UserLoginPostRes.of(200, "Success", JwtTokenUtil.getAccessToken(userEmail)));
        }
        // 유효하지 않는 패스워드인 경우, 로그인 실패로 응답.
        return ResponseEntity.status(401).body(UserLoginPostRes.of(401, "Invalid Password", null));
    }

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = UserLoginPostRes.class),
            @ApiResponse(code = 401, message = "토큰 없음", response = BaseResponseBody.class),
            @ApiResponse(code = 404, message = "요청 실패", response = BaseResponseBody.class),
            @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)
    })
    public ResponseEntity<BaseResponseBody> logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "Cookies is null"));
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        // 쿠키 목록에 refreshToken 이 없으면 요청 실패 에러
        if (refreshToken == null) {
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "Not Exist refreshToken"));
        }

        // DB에 refreshToken 이 있으면 refreshToken 삭제 후 로그아웃

        String email = userService.patchUserDeleteTokenByrefreshToken(refreshToken);
        if (email != null) {

            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            return ResponseEntity.ok(BaseResponseBody.of(200, "Success"));
        }

        // DB에 refreshToken 이 없으면 토큰 없음 에러
        return ResponseEntity.status(401).body(BaseResponseBody.of(401, "Invalid Token"));
    }

    @PostMapping("/reissue")
    @ApiOperation(value = "access 토큰 재발급", notes = "access 토큰을 재발급한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = UserLoginPostRes.class),
            @ApiResponse(code = 401, message = "토큰 없음", response = BaseResponseBody.class),
            @ApiResponse(code = 404, message = "요청 실패", response = BaseResponseBody.class),
            @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)
    })
    public ResponseEntity<UserLoginPostRes> reIssue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(404).body(UserLoginPostRes.of(404, "Cookies is null", null));
        }
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        // 쿠키 목록에 refreshToken 이 없으면 요청 실패 에러
        if (refreshToken == null) {
            return ResponseEntity.status(404).body(UserLoginPostRes.of(404, "Not Exist refreshToken", null));
        }

        // DB에 refreshToken 이 있으면 토큰재발급
        String token = userService.getUserTokenByRefreshToken(refreshToken);

        if (token != null) {
            DecodedJWT decodedJWT = JwtTokenUtil.getVerifier().verify(refreshToken.replace(JwtTokenUtil.TOKEN_PREFIX, ""));
            String email = decodedJWT.getSubject();
            return ResponseEntity.ok(UserLoginPostRes.of(200, "Success", JwtTokenUtil.getAccessToken(email)));
        }

        // DB에 refreshToken 이 없으면 토큰 없음 에러
        return ResponseEntity.status(401).body(UserLoginPostRes.of(401, "Invalid Token", null));
    }
}
