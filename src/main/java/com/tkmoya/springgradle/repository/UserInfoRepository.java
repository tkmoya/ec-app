package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.OnlineMemberEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository {
    List<OnlineMemberEntity> login(OnlineMemberEntity entity);

    List<OnlineMemberEntity> selectById(OnlineMemberEntity entity);

    List<OnlineMemberEntity> findAnsByMemberNo(int memberNo);

    int register(OnlineMemberEntity entity);

    Integer findMaxMemberNo();

    int updateUser(OnlineMemberEntity entity, String nowPassword);

    int delUser(OnlineMemberEntity entity);
}
