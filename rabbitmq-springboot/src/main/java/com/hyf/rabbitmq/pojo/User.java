package com.hyf.rabbitmq.pojo;

/**
 * 消息对象需要实现序列化接口或手动转为json
 * <p>
 * 对象传输时，包名、类名必须保持一致
 *
 * @author baB_hyf
 * @date 2021/02/17
 */
public class User {

    private Integer userId;
    private String  userName;

    public User() {
    }

    public User(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
