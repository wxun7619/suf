package com.lonntec.domainservice.lang;

import team.benchem.framework.lang.StateCode;

public enum DomainSystemStateCode implements StateCode {
    Domain_IsNotExist(500000,"企业不存在"),
    OwnerUserId_IsEmpty(500001,"实施人员不存在"),
    DomainName_IsEmpty(500002,"企业名称不能为空"),
    DomainShortName_IsEmpty(500003,"企业简称不能为空"),
    Address_IsEmpty(500004,"联系地址不能为空"),
    LinkMan_IsEmpty(500005,"联系人不能为空"),
    LinkManMobile_IsEmpty(500006,"联系人电话不能为空"),
    Login_IsNot(200062,"请先登录"),
    IsEnable_IsEmpty(500008,"是否启用不能为空"),
    DomainName_IsExist(500009,"企业名称已存在"),
    No_Permissions(500010,"没有权限"),
    NewOwnerUserId_IsEmpty(500011,"修改后的实施人员不存在")

    ;

    private Integer code;
    private String message;

    DomainSystemStateCode(Integer code, String message){

        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
