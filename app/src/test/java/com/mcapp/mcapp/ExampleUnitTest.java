package com.mcapp.mcapp;

import com.mcapp.mcapp.utils.FaceApp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testDetectFaceWithUrl(){
        String rs= FaceApp.detectFaceWithUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572672288501&di=eb1e8e3f41aad3e19608642d4ed4cdd8&imgtype=0&src=http%3A%2F%2Fm.360buyimg.com%2Fpop%2Fjfs%2Ft24973%2F327%2F220378424%2F35193%2F3e050ea0%2F5b694f84N1160431a.jpg");
        System.out.println(rs);
    }

}