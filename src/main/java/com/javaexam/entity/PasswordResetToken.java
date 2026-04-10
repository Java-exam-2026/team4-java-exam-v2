package com.javaexam.entity;
/**
 * パスワードリセット用トークンを管理するエンティティクラス
 * 
 * ＠author 川端理央
 */
public class PasswordResetToken {
    /** トークンを一意に識別するID */
    private String id;
    /** トークンの対象となるユーザーのID */
    private String userId;
    /** パスワードリセット用のランダムな文字列 */
    private String token;
    /** トークンの有効期限 */
    private String expiryDate;

    /** 
     * IDを取得する
     * 
     * @return ID
     */
    public String getId() {return id;}

    /** 
     * IDをセットする
     * 
     * ＠param id セットするID
     */
    public void setId(String id) {this.id = id;}

    /** 
     * ユーザーIDを取得する
     * 
     * @return ユーザーID
     */ 

    public String getUserId() {return userId;}
    /** 
     * ユーザーIDをセットする
     * 
     * ＠param userId セットするユーザーID
     */
    public void setUserId(String userId) {this.userId = userId;}
    /** 
     * トークンを取得する
     * 
     * @return トークン
     */
    public String getToken() {return token;}
    /** 
     * トークンをセットする
     * 
     * ＠param token セットするトークン
     */
    public void setToken(String token) {this.token = token;}

    /** 
     * 有効期限を取得する
     * 
     * @return 有効期限
    */
    public String getExpiryDate() {return expiryDate;}
    /** 
     * 有効期限をセットする
     * 
     * ＠param expiryDate セットする有効期限
     */
    public void setExpiryDate(String expiryDate) {this.expiryDate = expiryDate;}



}
