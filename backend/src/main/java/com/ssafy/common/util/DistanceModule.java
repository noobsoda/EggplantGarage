package com.ssafy.common.util;

import com.ssafy.api.response.LiveContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DistanceModule {
    private double distance;
    private LiveContent liveContent;
}
