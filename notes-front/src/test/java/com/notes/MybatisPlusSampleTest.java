package com.notes;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.notes.domain.front.user.NotesUser;
import com.notes.mapper.front.user.NotesUserMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MapperScan("com.notes.mapper.front.user")
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.url=jdbc:mysql://localhost:3306/xlls-notes?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8",
    "spring.datasource.username=root",
    "spring.datasource.password=123456"})
class MybatisPlusSampleTest {

    @Autowired
    private NotesUserMapper notesUserMapper;
    @Test
    void testInsert() {
        NotesUser notesUser = notesUserMapper.selectById(1);
        System.out.println(notesUser);
    }
}