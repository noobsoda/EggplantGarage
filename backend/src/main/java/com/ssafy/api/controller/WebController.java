package com.ssafy.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController{

    //리액트 url 라우팅 설정
    @GetMapping(value = {"", "/home", "/submit", "/seller/**", "/login", "/signup", "/signupemail", "/search", "/like", "/chat/**", "/mypage", "category"
    , "infoedit", "liveshowdetail", "writereview", "review", "liveshow/**", "/findpass"})
    public String forward() {
        return "forward:/index.html";
    }

}
