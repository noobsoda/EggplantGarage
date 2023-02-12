package com.ssafy.api.controller;

import com.ssafy.api.request.UserDeleteReq;
import com.ssafy.api.request.UserInfoPatchReq;
import com.ssafy.common.model.response.CommonResponse;
import com.ssafy.common.model.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ssafy.api.request.UserRegisterPostReq;
import com.ssafy.api.response.UserRes;
import com.ssafy.api.service.UserService;
import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.User;
//import com.ssafy.db.repository.UserRepositorySupport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 유저 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "유저 API", tags = {"User"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final Logger logger;
    private final UserService userService;
    private final ResponseService responseService;

    @PostMapping()
    @ApiOperation(value = "회원 가입", notes = "<strong>아이디와 패스워드</strong>를 통해 회원가입 한다.")
    @ApiResponses({@ApiResponse(code = 201, message = "생성 성공"), @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "사용자 없음"), @ApiResponse(code = 500, message = "서버 오류")})
    public ResponseEntity<? extends CommonResponse> register(@RequestBody @ApiParam(value = "회원가입 정보", required = true) UserRegisterPostReq registerInfo) {

        // 임의로 리턴된 User 인스턴스. 현재 코드는 회원 가입 성공 여부만 판단하기 때문에 굳이 Insert 된 유저 정보를 응답하지 않음.
        userService.createUser(registerInfo);

        return ResponseEntity.status(201).body(responseService.getSuccessResponse(201, "회원가입 성공"));
    }

    @GetMapping("/me")
    @ApiOperation(value = "회원 본인 정보 조회", notes = "로그인한 회원 본인의 정보를 응답한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "사용자 없음"), @ApiResponse(code = 500, message = "서버 오류")})
    public ResponseEntity<UserRes> getUserInfo(@ApiIgnore Authentication authentication) {
        /*
         * 요청 헤더 액세스 토큰이 포함된 경우에만 실행되는 인증 처리이후, 리턴되는 인증 정보 객체(authentication) 통해서 요청한 유저
         * 식별.
         * 액세스 토큰이 없이 요청하는 경우, 403 에러({"error": "Forbidden", "message": "Access
         * Denied"}) 발생.
         */
        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
        String userEmail = userDetails.getUsername();
        User user = userService.getUserByEmail(userEmail);

        return ResponseEntity.status(200).body(UserRes.of(user));
    }

    @GetMapping("/email/{email}")
    @ApiOperation(value = "사용가능한 이메일 확인", notes = "입력한 이메일이 사용 가능한지 확인한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "사용 가능"), @ApiResponse(code = 409, message = "사용자 존재"),})
    //유저 이메일 중복이 있는지 확인하는 메소드
    public ResponseEntity<? extends BaseResponseBody> checkUserEmail(@PathVariable("email") String email) {


        User user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.status(409).body(BaseResponseBody.of(409, "이미 존재하는 사용자 ID입니다.", false));
        } else {
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "현재 ID는 사용 가능합니다", true));
        }

    }

    @GetMapping("/nickname/{nickname}")
    @ApiOperation(value = "사용가능한 닉네임 확인", notes = "입력한 닉네임이 사용 가능한지 확인한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "사용 가능"), @ApiResponse(code = 409, message = "사용자 존재"),})
    //유저 닉네임 중복이 있는지 확인하는 메소드
    public ResponseEntity<? extends BaseResponseBody> checkUserNickname(@PathVariable("nickname") String nickname) {

        User user = userService.getUserByNickname(nickname);
        if (user != null) {
            return ResponseEntity.status(409).body(BaseResponseBody.of(409, "이미 존재하는 사용자 닉네임입니다.", false));
        } else {
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "현재 닉네임은 사용 가능합니다", true));
        }

    }

    @PatchMapping("/delete/{email}")
    @ApiOperation(value = "회원 본인 정보 삭제", notes = "로그인한 회원 본인의 정보를 삭제한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 401, message = "인증 실패"),})
    public ResponseEntity<? extends BaseResponseBody> deleteUser(@RequestBody @ApiParam(value = "회원탈퇴 정보", required = true) UserDeleteReq userDeleteReq,
                                                                 @PathVariable("email") String email) {

        if (userService.deleteUserByEmail(email, userDeleteReq)) {
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success", true));
        } else {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "Unauthorized", false));
        }

    }

    @PatchMapping()
    @ApiOperation(value = "회원 정보 업데이트", notes = "로그인한 회원 본인의 정보를 수정한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 401, message = "인증 실패"),})
    public ResponseEntity<? extends BaseResponseBody> patchUserInfo(@ApiIgnore Authentication authentication, @RequestBody @ApiParam(value = "회원탈퇴 정보", required = true) UserInfoPatchReq userInfoPatchReq) {

        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
        String userEmail = userDetails.getUsername();

        if (userService.patchUserByEmail(userEmail, userInfoPatchReq)) {
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success", true));
        } else {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "Unauthorized", false));
        }


    }

}
