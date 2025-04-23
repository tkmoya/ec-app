package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.entity.OnlineMemberEntity;
import com.tkmoya.springgradle.exception.UserServiceException;
import com.tkmoya.springgradle.model.EditFormModel;
import com.tkmoya.springgradle.model.LoginFormModel;
import com.tkmoya.springgradle.model.RegisterFormModel;
import com.tkmoya.springgradle.repository.UserInfoRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserInfoRepositoryImpl userInfoRepository;

    /**
     * ログイン認証処理
     *
     * @param model ログインフォームモデル
     * @throws UserServiceException 認証失敗やシステムエラー時に発生
     */
    @Override
    public void doLogin(LoginFormModel model) throws UserServiceException {
        OnlineMemberEntity entity = new OnlineMemberEntity();
        entity.setMemberNo(Integer.parseInt(model.getMemberNo()));
        entity.setPassword(model.getPassword());

        List<OnlineMemberEntity> resultList = userInfoRepository.login(entity);

        if (resultList.isEmpty()) {
            throw new UserServiceException("ログイン情報が見つかりません");
        }

        if (resultList.size() > 1) {
            log.error("ログイン対象ユーザが複数存在しています。会員番号: {}", model.getMemberNo());
            throw new UserServiceException("システムエラーが発生しました");
        }

        resultList.getFirst();
    }

    /**
     * 会員番号によるユーザー情報取得
     *
     * @param memberNo 会員番号
     * @return ユーザー情報モデル、存在しない場合はnull
     */
    @Override
    public EditFormModel findUserAnsByMemberNo(int memberNo) {
        List<OnlineMemberEntity> userList = userInfoRepository.findAnsByMemberNo(memberNo);

        if (userList.size() != 1) {
            return null;
        }

        return convertEntityToEditFormModel(userList.getFirst());
    }

    /**
     * パスワード確認処理
     *
     * @param memberNo    会員番号
     * @param nowPassword 現在のパスワード
     * @return パスワードが一致する場合true
     */
    @Override
    public boolean nowPassCk(int memberNo, String nowPassword) {
        OnlineMemberEntity entity = new OnlineMemberEntity();
        entity.setMemberNo(memberNo);
        entity.setPassword(nowPassword);

        return !userInfoRepository.login(entity).isEmpty();
    }

    /**
     * ユーザー登録処理
     *
     * @param model 登録フォームモデル
     * @throws UserServiceException 登録失敗時に発生
     */
    @Override
    @Transactional
    public void register(RegisterFormModel model) throws UserServiceException {
        try {
            Integer maxMemberNo = userInfoRepository.findMaxMemberNo();
            model.setMemberNo(maxMemberNo.toString());

            OnlineMemberEntity entity = convertRegisterModelToEntity(model);

            int result = userInfoRepository.register(entity);
            if (result <= 0) {
                throw new UserServiceException("ユーザー登録に失敗しました");
            }

        } catch (Exception e) {
            log.error("ユーザー登録処理でエラーが発生しました", e);
            throw new UserServiceException("ユーザー登録に失敗しました", e);
        }
    }

    /**
     * ユーザー情報更新処理
     *
     * @param memberNo 会員番号
     * @param model    編集フォームモデル
     * @throws UserServiceException 更新失敗時に発生
     */
    @Override
    @Transactional
    public void updateUser(int memberNo, EditFormModel model) throws UserServiceException {
        try {
            OnlineMemberEntity entity = convertEditFormModelToEntity(memberNo, model);

            int result = userInfoRepository.updateUser(entity, model.getNowPassword());
            if (result <= 0) {
                throw new UserServiceException("ユーザー情報の更新に失敗しました");
            }

        } catch (Exception e) {
            log.error("ユーザー情報更新処理でエラーが発生しました: 会員番号={}", memberNo, e);
            throw new UserServiceException("ユーザー情報の更新に失敗しました", e);
        }
    }

    /**
     * ユーザー削除処理（論理削除）
     *
     * @param memberNo 会員番号
     * @throws UserServiceException 削除失敗時に発生
     */
    @Override
    @Transactional
    public void delUser(int memberNo) throws UserServiceException {
        try {
            OnlineMemberEntity entity = new OnlineMemberEntity();
            entity.setMemberNo(memberNo);

            int result = userInfoRepository.delUser(entity);
            if (result <= 0) {
                throw new UserServiceException("ユーザーの削除に失敗しました");
            }

        } catch (Exception e) {
            log.error("ユーザー削除処理でエラーが発生しました: 会員番号={}", memberNo, e);
            throw new UserServiceException("ユーザーの削除に失敗しました", e);
        }
    }

    /**
     * エンティティから編集フォームモデルへの変換
     */
    private EditFormModel convertEntityToEditFormModel(OnlineMemberEntity entity) {
        EditFormModel model = new EditFormModel();
        model.setMemberNo(String.valueOf(entity.getMemberNo()));
        model.setNowPassword(entity.getPassword());
        model.setName(entity.getName());
        model.setAge(String.valueOf(entity.getAge()));
        model.setSex(entity.getSex());
        model.setZip(entity.getZip());
        model.setAddress(entity.getAddress());
        model.setTel(entity.getTel());
        model.setRegisterDate(entity.getRegisterDate());
        return model;
    }

    /**
     * 登録フォームモデルからエンティティへの変換
     */
    private OnlineMemberEntity convertRegisterModelToEntity(RegisterFormModel model) {
        OnlineMemberEntity entity = new OnlineMemberEntity();
        entity.setMemberNo(Integer.parseInt(model.getMemberNo()));
        entity.setName(model.getName());
        entity.setPassword(model.getPassword());
        entity.setAge(Integer.parseInt(model.getAge()));
        entity.setSex(model.getSex());
        entity.setZip(model.getZip());
        entity.setAddress(model.getAddress());
        entity.setTel(model.getTel());
        return entity;
    }

    /**
     * 編集フォームモデルからエンティティへの変換
     */
    private OnlineMemberEntity convertEditFormModelToEntity(int memberNo, EditFormModel model) {
        OnlineMemberEntity entity = new OnlineMemberEntity();
        entity.setMemberNo(memberNo);
        entity.setName(model.getName());
        entity.setPassword(model.getNewPassword());
        entity.setAge(Integer.parseInt(model.getAge()));
        entity.setSex(model.getSex());
        entity.setZip(model.getZip());
        entity.setAddress(model.getAddress());
        entity.setTel(model.getTel());
        return entity;
    }
}