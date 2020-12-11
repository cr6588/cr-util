package com.cr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * create in 2019年11月14日
 * @category TODO
 * @author chenyi
 */
public class LambdaTest {

    class Example {

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Test
    public void test() {
        List<Example> list = new ArrayList<>();
        Example a = new Example();
        list.add(a);
        Map<Integer, String> result2 = list.stream().collect(Collectors.toMap(Example::getId, Example::getName));

    }

}
