package com.ssafy.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApprovalRes {
    private String aid, tid, cid, sid;
    private String partner_order_id, partner_user_id, payment_method_type;
    private String item_name, item_code, payload;
    private Integer quantity;
    private Date created_at, approved_at;

    public KakaoPayApprovalRes(String tid, String partner_order_id, String partner_user_id) {
        this.tid = tid;
        this.partner_order_id = partner_order_id;
        this.partner_user_id = partner_user_id;
    }

}
