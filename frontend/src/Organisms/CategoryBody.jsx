import React from "react";
import styled from "styled-components";
import Body from "../Atoms/Body";
import CategoryBox from "../Molecules/CategoryBox";
import { useNavigate } from "react-router-dom";

const FlexDiv = styled.div`
  width: 280px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
`;

export default function CategoryBody() {
  const categories = [
    {
      name: "인기",
      src: "/image/category/star.png",
    },
    {
      name: "디지털기기",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "생활가전",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "가구",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "생활/주방",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "유아용품",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "유아도서",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "여성의류",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "여성잡화",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "남성의류",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "남성잡화",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "뷰티/미용",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "스포츠",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "취미/게임",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "음반",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "도서",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "티켓",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "반려동물",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "식물",
      src: "/image/tapbar/home-icon.svg",
    },
    {
      name: "기타",
      src: "/image/tapbar/home-icon.svg",
    },
  ];
  const navigate = useNavigate();
  function goTo(index) {
    navigate("/search", { state: { isResult: "True" } });
  }
  //props로 카테고리 전달해주기

  //   인덱스로 이동하게끔 하자 따로 영어네이밍 하는게 귀찮아용 힝힝
  return (
    <Body>
      <FlexDiv>
        {categories.map((box, index) => {
          return (
            <CategoryBox
              name={box.name}
              //   imgSrc={box.src}
              imgSrc="./image/category/star.png"
              clicked={() => goTo()}
              //카테고리 전달해주기
            />
          );
        })}
      </FlexDiv>
    </Body>
  );
}