import React from "react";
import Header from "../../Templates/Layout/Header";
import Page from "../../Templates/Layout/Page";
import Body from "../../Templates/Layout/Body";
import ItemCard from "../../Molecules/Cards/ItemCard";
import { useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { getLiveDetail } from "../../util/api/liveApi";
import { getSalesItemHistory } from "../../util/api/productApi";

export default function LiveshowDetail() {
  const { state } = useLocation();
  //axios 통신후 데이터 뿌리기
  const [live, setLive] = useState(undefined);
  const [productList, setProductList] = useState(undefined);
  useEffect(() => {
    getLiveDetail(state, ({ data }) => {
      setLive(data);
    });
    //이게 돼야 리뷰 관리가 가능함.
    getSalesItemHistory(state, ({ data }) => {
      setProductList(data);
    });
  }, []);
  return (
    <Page>
      <Header isName="True" headerName="라이브쇼상세" />
      <Body>
        <div className="page-header">{live && live.title}</div>
        <div className="body1-regular">
          {live &&
            live.createAt.split(":")[0] + ":" + live.createAt.split(":")[1]}
        </div>
        <hr></hr>
        <div className="body1-header">판매제품 목록</div>
        {productList &&
          productList.map((item) => {
            return (
              <ItemCard
                key={item.id}
                item={item}
                buttonType={"purchasedhistory"}
                isSeller={true}
                isReview={item.myReviewId === 0}
              />
            );
          })}
      </Body>
    </Page>
  );
}
