import React, { useEffect, useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import Header from "../../Templates/Layout/Header";
import Page from "../../Templates/Layout/Page";
import getStompClient from "../../util/socket";
import Body from "../../Templates/Layout/Body";
import styled from "styled-components";
import ChattingMessage from "../../Organisms/Chat/ChattingMessage";
import { checkUserInfo } from "../../store/user";
import { getChatMessageList } from "../../util/api/chatApi";

const StyledContainer = styled.div`
  width: 100%;
  height: 40px;
  display: flex;
  flex-direction: row;
  column-gap: 8px;
  align-items: center;
`;
const StyledInput = styled.input`
  width: calc(100% - 32px);
  height: 40px;
  border: 2px solid ${({ theme }) => theme.color.red};
  border-radius: 8px;
  box-sizing: border-box;
  background-color: rgba(255, 255, 255, 0);
  color: ${({ theme }) => theme.color.red};
  padding: 0 8px;
  &::placeholder {
    /* Chrome, Firefox, Opera, Safari 10.1+ */
    color: ${({ theme }) => theme.color.red};
    opacity: 1; /* Firefox */
  }
`;
const SendBtn = styled.button`
  width: 24px;
  height: 24px;
  background: url("/image/send-icon.svg") no-repeat 0px 0px;
`;
const ChatBody = styled.div`
  height: calc(100% - 56px);
  display: flex;
  flex-direction: column;
  row-gap: 8px;
`;

export default function ChatRoom() {
  const userInfo = useSelector(checkUserInfo);
  const senderId = userInfo.id;
  const receiverId = useLocation().state.receiverId;
  const receiverName = useLocation().state.receiverName;
  const chatRoomId = useLocation().state.chatRoomId; // 현재 URL을 통해 RoomId를 얻어옴
  const [chatMessagesList, setChatMessagesList] = useState([]); // 주고 받은 메시지 리스트
  const [message, setMessage] = useState(""); // 입력창 메시지
  const [stompClient] = useState(getStompClient());
  let navigate = useNavigate();
  const dispatch = useDispatch();

  // 메시지 배열에 새로운 메시지 추가
  const addMessage = (message) => {
    setChatMessagesList((prev) => [...prev, message]);
  };

  // 메시지 받기 : 받은 메시지를 메시지 배열에 추가
  // stomp는 텍스트 처리만 가능하기 때문에 보낼 때 JSON.stringify(newMessage)) 받을 때 JSON.parse(data.body)처리를 꼭 해주어야 함
  function connect() {
    stompClient.connect(
      {},
      () => {
        stompClient.subscribe("/sub/room/" + chatRoomId, (data) => {
          const newMessage = JSON.parse(data.body);
          addMessage(newMessage);
        });
      },
      () => {
        console.warn("message fail");
      }
    );
  }

  // 처음 컴포넌트가 새롭게 생성되는 시점에 한 번 실행
  // 백엔드 서버에 데이터를 요청할 때 axios 작업할 때 사용
  useEffect(() => {
    if (stompClient === null) {
      return;
    }
    connect();
    // dispatch의 내부에 있는 chattingMessageList라는 함수의 액션 객체를 반환 받아 setChattingMessages, setUserList 하도록 로직 추가 필요
    getChatMessageList(chatRoomId, ({ data }) => {
      setChatMessagesList(data);
    });
  }, []);

  // 의존성 변수 chattingMessages가 변경될 때만 함수 호출
  // 새로운 메시지가 생성될때 채팅 스크롤
  // 메시지가 추가될 경우 이벤트가 발생하여, 스크롤을 가장 밑으로 내림

  // 메시지 보내기
  const sendMessage = () => {
    if (message != "") {
      stompClient.send(
        "/pub/room/message",
        {},
        JSON.stringify({
          content: message,
          senderId: senderId,
          receiverId: receiverId,
          chatRoomId: chatRoomId,
          sendTime: null,
        })
      );
      // scrollRef.current.scrollIntoView({ behavior: "smooth", block: "end" });
    }
    setMessage(""); // 메시지 전송 후 문자열 초기화
  };

  // Enter를 누르면 메시지 보냄
  // 전송버튼 클릭시 전송도 추가 구현 필요!
  const onKeyPress = (e) => {
    if (e.key == "Enter") {
      sendMessage();
    }
  };

  return (
    <Page>
      <Header isName="True" headerName="채팅방" />
      {/* scrollRef를 이용하여 아래 div 영역을 스크롤 조작 */}
      <Body>
        <ChatBody>
          <div className="body1-header"> {receiverName}님과의 채팅방</div>
          <ChattingMessage
            chattingMessages={chatMessagesList}
            senderName={receiverName}
            myId={userInfo.id}
          />
        </ChatBody>
        <StyledContainer>
          <StyledInput
            // ref={InputRef}
            value={message}
            placeholder="메시지를 입력하세요"
            onChange={(e) => setMessage(e.target.value)}
            onKeyPress={onKeyPress}
          />
          <SendBtn onClick={sendMessage} />
        </StyledContainer>
      </Body>
    </Page>
  );
}
