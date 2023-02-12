import React from "react";
import styled from "styled-components";
import LeftBtn from "../../Atoms/IconButtons/LeftBtn";
import MenuBtn from "../../Atoms/IconButtons/MenuBtn";
import SearchInput from "../../Atoms/Inputs/SearchInput";
import { useNavigate } from "react-router-dom";
import SearchBtn from "../../Atoms/IconButtons/SearchBtn";

const StyledHeader = styled.div`
  width: 100%;
  padding: 0 16px;
  height: 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: solid 0.5px;
  border-bottom-color: ${({ theme }) => theme.color.lightgrey};
  box-sizing: border-box;
`;

export default function Header({
  isLogo,
  isSearch,
  isName,
  headerName,
  search,
  onChangeSearch,
}) {
  const navigate = useNavigate();
  function menuClicked() {
    navigate("/category");
  }
  return (
    <StyledHeader>
      <LeftBtn buttonClick={() => navigate(-1)} />
      {isSearch ? <SearchInput onChange={onChangeSearch} /> : <></>}
      {isSearch ? <SearchBtn buttonClick={search} /> : <></>}
      {isLogo ? (
        <img width="112" height="27" src="/image/logo.png" alt="" />
      ) : (
        <></>
      )}

      {isLogo ? <MenuBtn buttonClick={() => menuClicked()} /> : <></>}
      {isName ? <div className="page-header">{headerName}</div> : <></>}
      {isName ? <div></div> : <></>}
    </StyledHeader>
  );
}
