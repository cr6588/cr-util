package com.cdzy.cr.proxy;

import org.junit.Test;

public class ProxyTest {

    @Test
    public void test() {
        IHello hello = FacadeProxy.newMapperProxy(IHello.class);  
        System.out.println(hello.say("hello world"));
    }

}
