package cn.itcast.user.vo;


import lombok.Data;

import java.util.List;

@Data
public class OrderResultVO {
    private String UserName;
    private List<?> orderList;
}
