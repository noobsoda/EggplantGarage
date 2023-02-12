package com.ssafy.api.controller;

import com.ssafy.api.request.*;
import com.ssafy.api.response.LiveContent;
import com.ssafy.api.response.LiveDetailGetRes;
import com.ssafy.api.response.LiveListGetRes;
import com.ssafy.api.service.FileService;
import com.ssafy.api.service.LiveService;
import com.ssafy.api.service.UserService;
import com.ssafy.common.error.ErrorCode;
import com.ssafy.common.exception.CustomException;
import com.ssafy.common.model.response.CommonResponse;
import com.ssafy.common.model.response.ResponseService;
import com.ssafy.common.util.DistanceModule;
import com.ssafy.common.util.LocationDistance;
import com.ssafy.db.entity.Live;
import com.ssafy.db.entity.User;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 라이브 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "라이브 API", tags = {"Live"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lives")
public class LiveController {
    private final Logger logger;
    private final UserService userService;
    private final LiveService liveService;
    private final FileService fileService;
    private final ResponseService responseService;


    @PostMapping("")
    @ApiOperation(value = "방 생성", notes = "방을 생성한다.")
    @ApiResponses({@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 401, message = "만료됨"), @ApiResponse(code = 403, message = "인증 실패"), @ApiResponse(code = 500, message = "서버 오류")})
    public ResponseEntity<Map<String, Object>> postLiveCreate(@RequestBody @ApiParam(value = "방 생성 정보", required = true) LiveRegisterPostReq liveRegisterInfo) {

        Map<String, Object> resMap = new HashMap<>();
        //유저 확인
        User user = userService.getUserById(liveRegisterInfo.getSellerId());
        //세션아이디 중복 체크
        /*if (liveService.getLiveCheckSessionIdBySessionId(liveRegisterInfo.getSessionId())) {
            resMap.put("statusCode", 409);
            resMap.put("message", "세션 ID가 중복됩니다");
            return ResponseEntity.status(409).body(resMap);
        }*/
        //db에 저장 및 생성
        Live live = liveService.CreateLive(liveRegisterInfo, user);
        resMap.put("liveId", live.getId());
        return ResponseEntity.status(201).body(resMap);


    }

    @PostMapping("/save/img")
    @ApiOperation(value = "이미지 저장", notes = "이미지 DB 저장 후, idx 반환")
    public CommonResponse postSaveImg(MultipartFile img, @RequestParam Long liveId) {
        if (img == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        String filename = fileService.filename(img);
        fileService.fileSave(img, filename);
        //이메일로 아이디 찾고
        //그 아이디로 셀러 아이디 조회하고 해당 객체에 이미지 넣기
        liveService.postLiveByThumbnailUrl(liveId, filename);
        return responseService.getSuccessResponse(200, "이미지 넣기 성공");


    }

    @PostMapping("/detail")
    @ApiOperation(value = "방 상세정보 조회", notes = "방의 상세 정보와 유저 목록을 조회한다.")
    public ResponseEntity<? extends LiveDetailGetRes> getLiveDetailInfo(@RequestBody HashMap<String, Long> sessionMap) {
        Long liveId = sessionMap.get("liveId");
        if (liveId == null) {
            throw new CustomException(ErrorCode.LIVE_NOT_FOUND);
        }
        LiveDetailGetRes liveDetailGetRes = liveService.getLiveDetailBySessionId(liveId);
        //상품도 추가로 보여주기

        return ResponseEntity.status(200).body(liveDetailGetRes);

    }

    //카테고리 넣기
    @PostMapping("/category")
    @ApiOperation(value = "방 카테고리 저장", notes = "방의 카테고리를 저장한다..")
    public CommonResponse postLiveCategory(@RequestBody @ApiParam(value = "방 생성 정보", required = true) LiveCategoriesReq liveCategoriesReq) {
        if(liveCategoriesReq.getLiveId() == null)
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        liveService.postLiveByCategories(liveCategoriesReq);
        return responseService.getSuccessResponse(200, "카테고리 넣기 성공");

    }

    @PostMapping("/search")
    @ApiOperation(value = "방 검색 목록 조회", notes = "모든 방의 검색 목록을 조회한다. 전국 true, 지역 false    ASC, DESC 대소문자 상관 없음, 위도 경도 아무값도 안줄시 싸피캠퍼스")
    public ResponseEntity<LiveListGetRes> getLiveSearchListInfo(@RequestBody @ApiParam(value = "방 검색 정보", required = true) LiveAllInfoGetReq liveAllInfoGetReq) {
        List<LiveContent> liveContentList;
        //제목기준으로 방 목록 조회하기 제목이 없으면 전체 조회
        if (StringUtils.trimToEmpty(liveAllInfoGetReq.getTitle()).equals("")) {
            liveContentList = liveService.getLiveListByTitle("");
        } else {
            liveContentList = liveService.getLiveListByTitle(liveAllInfoGetReq.getTitle());
        }

        //카테고리 기준으로 방 목록 조회하기, 카테고리 설정 안하면 넘어가기
        if (!StringUtils.trimToEmpty(liveAllInfoGetReq.getCategory()).equals("")) {
            liveContentList = liveService.searchCategoryLiveList(liveContentList, liveAllInfoGetReq.getCategory());
        }

        Location location = Location.builder()
                .latitude(liveAllInfoGetReq.getLatitude())
                .longitude(liveAllInfoGetReq.getLongitude())
                .build();

        //전국인지, 5km인지 isNational을 통해 구분해서 distance 포함된 값 반환
        List<DistanceModule> distanceModuleList;
        distanceModuleList = liveService.searchLocationLiveList(liveContentList, location, liveAllInfoGetReq.isNational());

        //거리별 정렬
        String distanceSort = StringUtils.trimToEmpty(liveAllInfoGetReq.getDistanceSort());
        distanceSort = StringUtils.upperCase(distanceSort);

        //ASC, DESC가 아니면 그냥 값 들어감
        liveContentList = LocationDistance.distanceSort(distanceModuleList, distanceSort);


        String userJoinSort = StringUtils.trimToEmpty(liveAllInfoGetReq.getJoinUserSort());
        userJoinSort = StringUtils.upperCase(userJoinSort);
        //유저별 정렬
        if (!userJoinSort.equals("")) {
            liveContentList = liveService.searchSortUserJoinLiveList(liveContentList, userJoinSort);
        }

        if (liveAllInfoGetReq.getPage() > 0) {
            liveContentList = liveService.setPageaLiveList(liveContentList, liveAllInfoGetReq.getPage());
        }


        if (liveContentList == null) {
            throw new CustomException(ErrorCode.SEARCH_NOT_FOUND);
        } else {
            return ResponseEntity.status(200).body(LiveListGetRes.of(liveContentList));
        }

    }

    //참가자 조인
    @PostMapping("/userlive")
    @ApiOperation(value = "유저 방 참가", notes = "유저의 방 참가")
    public CommonResponse postLiveUserJoin(@RequestBody @ApiParam(value = "유저 참가 정보", required = true) LiveUserJoinReq LiveUserJoinReq) {

        liveService.postUserLiveByLiveId(LiveUserJoinReq);
        return responseService.getSuccessResponse(200, "라이브 참가 성공");
    }

    @DeleteMapping("/userlive")
    @ApiOperation(value = "유저 방 퇴장", notes = "유저의 라이브 퇴장")
    public CommonResponse deleteLiveUserQuit(@RequestBody @ApiParam(value = "유저 참가 정보", required = true) LiveUserJoinReq LiveUserJoinReq) {

        liveService.deleteUserLiveByLiveId(LiveUserJoinReq);
        return responseService.getSuccessResponse(200, "라이브 나가기 성공");

    }

    //라이브 종료
    @PatchMapping("/{liveid}")
    @ApiOperation(value = "라이브 끝내기", notes = "해당 라이브를 끝냅니다")
    public CommonResponse patchLiveEnd(@PathVariable("liveid") Long liveId) {

        //방 목록 조회하기
        liveService.patchLiveEndById(liveId);
        return responseService.getSuccessResponse(200, "라이브를 성공적으로 끝냈습니다");


    }


}

