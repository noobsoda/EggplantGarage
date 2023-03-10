import React from "react";
import styled, {css} from "styled-components";

const FlexBox = styled.div`
  display: flex;
  flex-direction: row;
  column-gap: 8px;
  align-items: center;
`;
const Img = styled.div`
  width: 20px;
  height: 20px;
  background: url("/image/categorycheckedfalse.svg") no-repeat 0px 0px;

  ${(props) =>
    props.isClicked === true &&
    css`
    background-position-y: 20px;
    background: url("/image/categorycheckedtrue.svg") no-repeat 0px 0px;
    `};
`;
const StyledCategoryBtn = styled.div`
  // 이부분이 잘모르겠는게 안에 텍스트 내용에 따라서 사이즈가 바뀔것.
  height: 24px;
  display: flex;
  align-items: center;
  color: ${({ theme }) => theme.color.grey};
`;

export default function CategoryCheckBtn({
  name = "전체",
  buttonClick,
  isClicked,
}) {
  return (
    <FlexBox onClick={buttonClick}>
      <Img isClicked={isClicked}></Img>
      <StyledCategoryBtn
        className="body1-header"
        style={{ color: isClicked ? "white" : "grey" }}
      >
        {name}
      </StyledCategoryBtn>
    </FlexBox>
  );
}
