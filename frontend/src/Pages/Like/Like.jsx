import React, { useEffect } from "react";
import Header from "../../Templates/Layout/Header";
import Body from "../../Templates/Layout/Body";
import LikeBody from "../../Organisms/Like/LikeBody";
import Page from "../../Templates/Layout/Page";
import { useState } from "react";
import { getLikeLives } from "../../util/api/favoriteApi";
import { useSelector } from "react-redux";
import { checkUserInfo } from "../../store/user";

export default function Like() {
  const [lives, setLives] = useState([]);
  const userInfo = useSelector(checkUserInfo);

  useEffect(() => {
    getLikeLives(userInfo.id, ({ data }) => {
      console.log(userInfo.id);
      console.log(data.liveContentList);
      setLives(data.liveContentList);
    });
  }, []);
  return (
    <>
      <Page>
        <Header isName="True" headerName="좋아요 목록" />
        <Body>{<LikeBody lives={lives} />}</Body>
      </Page>
    </>
  );
}
