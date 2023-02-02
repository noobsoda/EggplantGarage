import React from "react";
import styled from "styled-components";
import CategoryBtn from "../Atoms/Buttons/CategoryBtn";

const StyledCategoryNav = styled.div`
  width: 280px;
  height: 40px;
  display: flex;
`;
/*
	live 내부에 thumbnail, viewercnt , seller_nickname,liveshow_title 등등 뽑혀야됨  
*/
export default function CategoryNav({ liveshow, buttonClick }) {
  //useState , event
  return (
    <StyledCategoryNav>
      <CategoryBtn></CategoryBtn>
      <CategoryBtn></CategoryBtn>
      <CategoryBtn></CategoryBtn>
      <CategoryBtn></CategoryBtn>
    </StyledCategoryNav>
  );
}