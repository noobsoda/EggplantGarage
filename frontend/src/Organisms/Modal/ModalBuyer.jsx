import React from "react";
import styled from "styled-components";
import SmallBtn from "../../Atoms/Buttons/SmallBtn";
import Modal from "../../Atoms/Modal/Modal";
import { useState } from "react";
import SalesList from "../../Templates/Modal/BuyerBody/SalesList";
import SuggestionList from "../../Templates/Modal/BuyerBody/SuggestionList";
import PayList from "../../Templates/Modal/BuyerBody/PayList";
import ModalBody from "../../Templates/Modal/ModalBody";

const Header = styled.div`
  width: 100%;
  display: flex;
  color: ${({ theme }) => theme.color.white};
`;
const CloseBtn = styled.button`
  width: 40px;
  height: 24px;
  //   background: url("/image/close.svg") norepeat 24px 16px;
  background: url("/image/close.svg");
  background-repeat: no-repeat;
  background-position: 16px 0px;
`;
const FlexBox = styled.div`
  display: flex;
  column-gap: 8px;
  justify-content: flex-end;
`;
export default function ModalBuyer({
  productList,
  bundleList,
  setModalOpen,
  isSeller,
  userId,
  liveId,
  getSuggest,
  getApprovSuggest,
  payList,
}) {
  const closeModal = () => {
    setModalOpen(false);
  };
  const apply = () => {
    closeModal();
  };
  const [isSuggestion, setIsSuggestion] = useState(false);
  const [isPay, setIsPay] = useState(false);
  return (
    <Modal>
      <Header>
        <CloseBtn onClick={closeModal} />
        <div className="page-header">물건목록</div>
      </Header>
      <ModalBody>
        <FlexBox>
          {payList !== undefined && (
            <SmallBtn
              name="구매 목록"
              buttonClick={() => {
                setIsPay(true);
              }}
            />
          )}

          <SmallBtn
            name="제안목록"
            buttonClick={() => {
              setIsPay(false);
              setIsSuggestion(false);
            }}
          />
          <SmallBtn
            name="판매목록"
            buttonClick={() => {
              setIsPay(false);
              setIsSuggestion(true);
            }}
          />
        </FlexBox>
        {isPay ? (
          <PayList
            liveId={liveId}
            userId={userId}
            payList={payList}
            getPayList={getApprovSuggest}
          />
        ) : isSuggestion ? (
          <SalesList
            liveId={liveId}
            userId={userId}
            isSeller={isSeller}
            productList={productList}
          />
        ) : (
          <SuggestionList
            liveId={liveId}
            userId={userId}
            isSeller={isSeller}
            suggestList={bundleList}
            getSuggest={getSuggest}
          />
        )}
      </ModalBody>
    </Modal>
  );
}