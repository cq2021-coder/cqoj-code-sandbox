package com.cq.sandbox.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DockerDaoTest {

    @Resource
    private DockerDao dockerDao;

    @Test
    public void testPullImage() {
//        String image = "nginx:latest";
//        dockerDao.pullImage(image);
    }
}