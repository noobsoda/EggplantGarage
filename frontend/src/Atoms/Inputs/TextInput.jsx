import React from "react";
import styled from "styled-components";

const StyledInput = styled.input`
  width: calc(100% - 8px);
  height: 80px;

  border: 2px solid ${({ theme }) => theme.color.darkgrey};
  border-radius: 8px;
  margin-bottom: 8px;
  color: ${({ theme }) => theme.color.black};
`;

export default function TextInput({ placehold, onChange, type }) {
  return (
    <StyledInput
      type={type}
      className="body1-regular"
      onChange={onChange}
      placeholder={placehold}
    ></StyledInput>
  );
}
