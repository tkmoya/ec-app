package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.model.EditFormModel;
import com.tkmoya.springgradle.model.LoginFormModel;
import com.tkmoya.springgradle.model.RegisterFormModel;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    /**
     * 認証処理
     */
    void doLogin(LoginFormModel model) throws Exception;

    /**
     * ユーザ情報取得処理
     */
    EditFormModel findUserAnsByMemberNo(int memberNo);


    /**
     * パスワード確認処理
     */
    boolean nowPassCk(int memberNo, String nowPassword);

    /**
     * 登録処理
     */
    @Transactional(rollbackForClassName = "Exception")
    void register(RegisterFormModel model) throws Exception;

    /**
     * ユーザ情報変更処理
     */
    @Transactional(rollbackForClassName = "Exception")
    void updateUser(int memberNo, EditFormModel model) throws Exception;

    /**
     * ユーザー削除処理（論理削除）
     */
    @Transactional(rollbackForClassName = "Exception")
    void delUser(int memberNo) throws Exception;
}
