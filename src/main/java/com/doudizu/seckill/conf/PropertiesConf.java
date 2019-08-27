package com.doudizu.seckill.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PropertiesConf {
    @Value("${sharding.product}")
    private int productTableCount;
    @Value("${pay.url}")
    private String payUrl;
    @Value("${pay.port}")
    private int payPort;
    @Value("${pay.path}")
    private String payPath;
    @Value("${reset.token}")
    private String resetToken;
    @Value("${product.category}")
    private int productCategory;
}
