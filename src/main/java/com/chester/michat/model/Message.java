package com.chester.michat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private Integer po;//端口
    private String ip;//ip地址
    private String p;//该地址用的公钥
}
