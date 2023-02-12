package com.ssafy.api.controller;

import com.ssafy.api.request.BundleReq;
import com.ssafy.api.response.BundledItemsProductRes;
import com.ssafy.api.service.BundleService;
import com.ssafy.db.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bundle")
public class BundleController {

    private final BundleService bundleService;

    // 구매자가 가격 제안함
    @PostMapping()
    public ResponseEntity<?> addBundle(@RequestBody BundleReq bundleReq) {
        Long bundleId = bundleService.addBundle(bundleReq);
        return ResponseEntity.status(200).body("묶음 상품 등록 성공: " + bundleId);
    }

    // 판매자 - 묶음 제안 목록
    @GetMapping("/seller/{liveId}")
    public ResponseEntity<?> getSellerSuggestList(@PathVariable("liveId") Long liveId) {
        List<List<BundledItemsProductRes>> suggestList = bundleService.getSellerSuggestList(liveId);
        return ResponseEntity.status(200).body(suggestList);
    }

    // 구매자 - 묶음 제안 목록 (승인완료- 결제X)
    @GetMapping("/buyer/approvalNoPaid/{liveId}/{buyerId}")
    public ResponseEntity<?> getApprovalNoPaidSuggestList(@PathVariable("liveId") Long liveId, @PathVariable("buyerId") Long buyerId) {
        List<List<BundledItemsProductRes>> getProductList = bundleService.getApprovalNoPaidSuggestList(liveId, buyerId);
        return ResponseEntity.status(200).body(getProductList);
    }

    // 구매자 - 묶음 제안 목록 (승인완료- 결제O)
    @GetMapping("/buyer/approvalYesPaid/{liveId}/{buyerId}")
    public ResponseEntity<?> getApprovalYesPaidSuggestList(@PathVariable("liveId") Long liveId, @PathVariable("buyerId") Long buyerId) {
        List<List<BundledItemsProductRes>> getProductList = bundleService.getApprovalYesPaidSuggestList(liveId, buyerId);
        return ResponseEntity.status(200).body(getProductList);
    }

    // 구매자 - 묶음 제안 목록 (대기)
    @GetMapping("/buyer/{liveId}/{buyerId}")
    public ResponseEntity<?> getBuyerSuggestList(@PathVariable("liveId") Long liveId, @PathVariable("buyerId") Long buyerId) {
        List<List<BundledItemsProductRes>> getProductList = bundleService.getBuyerSuggestList(liveId, buyerId);
        return ResponseEntity.status(200).body(getProductList);
    }

    // 구매자 - 묶음 제안 목록 (거부)
    @GetMapping("/buyer/refuse/{liveId}/{buyerId}")
    public ResponseEntity<?> getRefuseSuggestList(@PathVariable("liveId") Long liveId, @PathVariable("buyerId") Long buyerId) {
        List<List<BundledItemsProductRes>> getProductList = bundleService.getRefuseSuggestList(liveId, buyerId);
        return ResponseEntity.status(200).body(getProductList);
    }


    // bundleId의 묶음 상품들 보기
    @GetMapping("/items/{bundleId}")
    public ResponseEntity<?> getBundledItemsRelation(@PathVariable("bundleId") Long bundleId) {
        List<Product> getProductList = bundleService.getBundleItemsList(bundleId);
        return ResponseEntity.status(200).body(getProductList);
    }

    // 승인
    @PatchMapping("/approval/{bundleId}")
    public ResponseEntity<?> approvalBundle(@PathVariable("bundleId") Long bundleId) {
        bundleService.approvalBundle(bundleId);
        return ResponseEntity.status(200).body("승인");
    }

    // 거부
    @PatchMapping("/refuse/{bundleId}")
    public ResponseEntity<?> refuseBundle(@PathVariable("bundleId") Long bundleId) {
        bundleService.refuseBundle(bundleId);
        return ResponseEntity.status(200).body("거부");
    }

    // 취소
    @PatchMapping("/cancel/{bundleId}")
    public ResponseEntity<?> cancelBundle(@PathVariable("bundleId") Long bundleId) {
        bundleService.cancelBundle(bundleId);
        return ResponseEntity.status(200).body("취소");
    }
}
