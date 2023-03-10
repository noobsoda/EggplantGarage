import React from "react";
import styled from "styled-components";
import LiveshowCard from "./LiveshowCard";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { joinLive } from "../../util/api/liveApi";

const StyledLiveshowItem = styled.div`
  width: calc(50% - 4px);
  height: ${(props) => {
    return props.isSearch ? "40%" : "100%";
  }};
  flex-grow: 0;
  flex-shrink: 0;
  // box-sizing: content-box;
  display: flex;
  flex-direction: column;
`;
const InfoBox = styled.div`
  display: flex;
  flex-direction: column;
`;
const StyledTitle = styled.div`
  overflow: hidden;
  white-space: nowrap;
  text-overflow: clip;
`;

/*
	live 내부에 thumbnail, viewercnt , seller_nickname,liveshow_title 등등 뽑혀야됨  
*/
export default function LiveshowItem({ isSearch, isViewer, show, isHistory }) {
  //useState , event
  const navigate = useNavigate();
  const user = useSelector((state) => state.user.userInfo);
  function goTo() {
    if (isViewer) {
      const joinReq = { userId: user.id, liveId: show.id };
      joinLive(joinReq);
      navigate(`/liveshow/${show.id}`);
    } else {
      navigate("/liveshowdetail", { state: show.liveId });
    }
  }
  return (
    <StyledLiveshowItem isSearch={isSearch} onClick={goTo}>
      <LiveshowCard
        viewerCnt={show.joinUsersNum}
        imgSrc={show.thumbnailUrl}
        isHistory={isHistory}
      />
      <InfoBox>
        {show && (
          <StyledTitle className="body1-header">
            {show.title || "라이브방 제목"}
          </StyledTitle>
        )}
        {/* show.createdAt */}
        {show && (
          <div className="body1-regular">
            {isHistory
              ? show.createdAt.split(":")[0] +
                  ":" +
                  show.createdAt.split(":")[1] || "판매날짜"
              : show.owner || "판매자명"}
          </div>
        )}
      </InfoBox>
    </StyledLiveshowItem>
  );
}
