package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.OnlineMemberEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserInfoRepositoryImpl implements UserInfoRepository {

    private final NamedParameterJdbcTemplate namedParameterTemplate;

    private final JdbcTemplate jdbcTemplate;

    public UserInfoRepositoryImpl(NamedParameterJdbcTemplate namedParameterTemplate) {
        this.namedParameterTemplate = namedParameterTemplate;
        this.jdbcTemplate = namedParameterTemplate.getJdbcTemplate();
    }

    @Override
    public List<OnlineMemberEntity> login(OnlineMemberEntity entity) {
        return this.namedParameterTemplate.query(
                "select * from ONLINE_MEMBER where MEMBER_NO = :memberNo and PASSWORD = :password and DELETE_FLG = '0'",
                new MapSqlParameterSource("memberNo", entity.getMemberNo()).addValue("password", entity.getPassword()),
                new BeanPropertyRowMapper<>(OnlineMemberEntity.class));
    }

    @Override
    public List<OnlineMemberEntity> selectById(OnlineMemberEntity entity) {
        return this.namedParameterTemplate.query(
                "select * from ONLINE_MEMBER where MEMBER_NO = :memberNo and DELETE_FLG = '0'",
                new MapSqlParameterSource("memberNo", entity.getMemberNo()),
                new BeanPropertyRowMapper<>(OnlineMemberEntity.class));
    }

    @Override
    public List<OnlineMemberEntity> findAnsByMemberNo(int memberNo) {
        return this.namedParameterTemplate.query(
                "select * from ONLINE_MEMBER where MEMBER_NO = :memberNo",
                new MapSqlParameterSource("memberNo", memberNo),
                new BeanPropertyRowMapper<>(OnlineMemberEntity.class));
    }

    @Override
    public int register(OnlineMemberEntity entity) {

        String sql = "insert into ONLINE_MEMBER (MEMBER_NO,NAME,PASSWORD,AGE,SEX,ZIP,ADDRESS,TEL,DELETE_FLG,REGISTER_DATE,LAST_UPD_DATE)  "
                + "values ( :memberNo, :name, :password, :age, :sex, :zip, :address, :tel, '0', NOW(), NOW())";
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("memberNo", findMaxMemberNo());
        paramMap.addValue("name", entity.getName());
        paramMap.addValue("password", entity.getPassword());
        paramMap.addValue("age", entity.getAge());
        paramMap.addValue("sex", entity.getSex());
        paramMap.addValue("zip", entity.getZip());
        paramMap.addValue("address", entity.getAddress());
        paramMap.addValue("tel", entity.getTel());
        return this.namedParameterTemplate.update(sql, paramMap);
    }

    public Integer findMaxMemberNo() {
        return this.jdbcTemplate.queryForObject("SELECT COALESCE(MAX(MEMBER_NO), 0) + 1 FROM ONLINE_MEMBER", Integer.class);
    }

    @Override
    public int updateUser(OnlineMemberEntity entity, String nowPassword) {
        String sql = "update ONLINE_MEMBER set NAME = :newName,AGE = :newAge,"
                + "SEX = :newSex,ZIP = :newZip,ADDRESS = :newAddress,TEL = :newTel";
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        if (!(entity.getPassword().isEmpty())) {
            sql += ",PASSWORD = :newPassword";
            paramMap.addValue("newPassword", entity.getPassword());
        }
        sql += ",LAST_UPD_DATE = NOW() where MEMBER_NO = :memberNo and PASSWORD = :nowPassword and DELETE_FLG = '0'";
        paramMap.addValue("newName", entity.getName());
        paramMap.addValue("newAge", entity.getAge());
        paramMap.addValue("newSex", entity.getSex());
        paramMap.addValue("newZip", entity.getZip());
        paramMap.addValue("newAddress", entity.getAddress());
        paramMap.addValue("newTel", entity.getTel());
        paramMap.addValue("memberNo", entity.getMemberNo());
        paramMap.addValue("nowPassword", nowPassword);
        return this.namedParameterTemplate.update(sql, paramMap);
    }

    @Override
    public int delUser(OnlineMemberEntity entity) {
        return this.namedParameterTemplate.update(
                "update ONLINE_MEMBER set DELETE_FLG = '1',LAST_UPD_DATE = NOW() where MEMBER_NO = :memberNo and DELETE_FLG = '0'",
                new MapSqlParameterSource("memberNo", entity.getMemberNo()));
    }
}
