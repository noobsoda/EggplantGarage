import React, { useEffect, useState } from "react";
import styled from "styled-components";
import ItemCard from "../../../Molecules/Cards/ItemCard";
import BigBtn from "../../../Atoms/Buttons/BigBtn";
import BigInput from "../../../Atoms/Inputs/BigInput";
import { postBundle } from "../../../util/api/productApi";

const InfoBox = styled.div`
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  row-gap: 16px;
  align-items: center;
`;

export default function SalesList({ userId, liveId, isSeller, productList }) {
  const [productCheck, setProductCheck] = useState(
    productList.map((ele) => {
      ele["check"] = false;
      return ele;
    })
  );

  const [bundlePrice, setBundlePrice] = useState(0);

  const changeHandler = (e, id) => {
    let checked = e.currentTarget.checked;
    let bundlePriceTmp = 0; //가격 계산
    setProductCheck(
      productCheck.map((ele) => {
        //기존 체크한거 계산
        if (ele.check) {
          bundlePriceTmp += ele.initialPrice;
        }
        if (ele.id === id) {
          if (checked) {
            //이번에 체크된거
            bundlePriceTmp += ele.initialPrice;
          } else {
            //이번에 체크 해제된경우 - 위에서 먼저 계산이 되었기 때문에
            bundlePriceTmp -= ele.initialPrice;
          }
          let tmpEle = { ...ele };

          tmpEle.check = checked;
          return tmpEle;
        }
        return ele;
      })
    );
    setBundlePrice(bundlePriceTmp);
  };

  /**
   * 지금까지 선택한 물품 구매 요청
   */
  function bundle() {
    postBundle(
      {
        productIdList: productCheck.filter((ele) => ele.check).map((ele) => ele.id), //check한것만 묶음 요청
        buyerId: userId,
        soldPrice: bundlePrice,
        liveId: liveId,
      },
      () => {
        //번들 요청 성공
        setBundlePrice(0); //가격 리셋
        //체크한것 다시 해제
        setProductCheck(
          productCheck.map((ele) => {
            let tmpEle = { ...ele };
            tmpEle["check"] = false;
            return tmpEle;
          })
        );
      },
      () => {
        console.warn("bundle fail");
      }
    );
  }

  function priceChange(e) {
    setBundlePrice(e.target.value);
  }
  return (
    <>
      {productCheck
        .map((ele) => {
          ele["key"] = ele.id;
          ele["productName"] = ele.name;
          ele["isSaleList"] = true;
          return ele;
        })
        .map((item) => {
          return (
            <ItemCard
              key={item.key}
              buttonType={isSeller ? undefined : "check"}
              isSeller={isSeller}
              item={item}
              check={item.check}
              isSaleList={item.isSaleList}
              setCheck={(e) => {
                changeHandler(e, item.id);
              }}
            />
          );
        })}
      {isSeller ? (
        <></>
      ) : (
        <InfoBox>
          <BigInput
            placehold={"예상금액"}
            textalign="right"
            value={bundlePrice}
            inputValue={priceChange}
          />
          <BigBtn name={"구매요청"} buttonClick={bundle} />
        </InfoBox>
      )}
    </>
  );
}
