import React from "react";
import styled from "styled-components";
import Tapbar from "./Tapbar";

const StyledPage = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
`;

export default function Page(props) {
  return (
    <StyledPage>
      {props.children}
      <Tapbar />
    </StyledPage>
  );
}
