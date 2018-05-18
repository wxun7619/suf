package team.benchem.usersystem.lang;

import team.benchem.framework.lang.StateCode;

public enum UserSystemStateCode implements StateCode {
    Channel_IsExist(200001, "频道菜单已存在"),
    Channel_IsNotExist(200002, "频道菜单不存在"),
    Group_IsExist(200003, "频道菜单下分组已存在"),
    Group_IsNotExist(200004, "频道菜单下分组不存在"),
    UserName_IsEmpty(200040,"用户名不能为空"),
    Email_IsEmpty(200041,"邮箱不能为空"),
    Mobile_IsEmpty(200042,"手机号码不能为空"),
    UserName_IsExites(200050, "用户名已存在"),
    Email_IsExites(200051,"此邮箱已注册"),
    Mobile_IsExites(200052,"此号码已占用"),
    User_IsNotExites(200053,"用户不存在"),
    OldPassword_isErr(200054,"旧密码输入不正确"),
    Password_IsReqeat(200055,"新密码不能与旧密码相同"),
    Password_IsEmpty(200056,"密码不能为空"),
    IsAdmin_IsEmpty(200057,"管理员标识不能为空"),
    IsEnable_IsEmpty(200058,"是否启用不能为空"),
    OldPassword_IsEmpty(200059,"请输入旧密码"),
    NewPassword_IsEmpty(200060,"新密码不能为空"),
    Password_IsErr(200061,"密码不正确"),
    User_IsDisabled(200062,"用户已禁用"),
    NickName_IsEmpty(200070,"昵称不能为空"),
    User_IsAdminNotDelete(200071,"用户为管理员,不允许删除"),
    User_IsAvail(200072,"删除用户前必须先禁用用户"),
    User_IsAdminNotDisabled(200073,"用户为管理员,不允许禁用"),
    No_Permission(200074,"没有权限"),
    Admin_CanNotDisableAdmin(200075,"admin账号不允许撤销管理员权限"),
    ;

    private Integer code;
    private String message;

    UserSystemStateCode(Integer code, String message){

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
