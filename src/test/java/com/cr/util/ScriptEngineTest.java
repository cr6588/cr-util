package com.cr.util;

import java.io.File;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;


/**
 * create in 2021年06月15日
 * @category TODO
 * @author chenyi
 */
public class ScriptEngineTest {

    @Test
    public void print() throws Exception{
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Object o = engine.eval("var o = {'a': 'a','b': 'adfasdfasd'};for(var i in o) {print('i=' + i + ',val=');} o;");
        System.out.println(o);
    }

    @Test
    public void obj() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        StringBuffer script = new StringBuffer();
        script.append("var obj = new Object();");
        script.append("obj.hello = function(name){print('hello, '+name);}");
        //执行这段script脚本
        engine.eval(script.toString());
        // javax.script.Invocable 是一个可选的接口
        // 检查你的script engine 接口是否已实现!
        // 注意：JavaScript engine实现了Invocable接口
        Invocable inv = (Invocable) engine;
        // 获取我们想调用那个方法所属的js对象
        Object obj = engine.get("obj");
        // 执行obj对象的名为hello的方法
        inv.invokeMethod(obj, "hello", "Script Method !!" );
    }

    @Test
    public void file() throws Exception{
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(new java.io.FileReader(new File("F:/test/test.js")));
        Invocable inv = (Invocable) engine;
        Object obj = engine.get("obj");
        inv.invokeMethod(obj, "name", "知道了" );
    }
    
    
    /**
     * 脚本变量
     * @throws Exception 
     */
    @Test
    public void scriptVar() throws Exception{
        String html = FileUtil.readTxtFile2StrByStringBuffer("D:\\test\\prods\\B08FC6Y4VG.html");
        Document doc = Jsoup.parse(html);
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
//        File file = new File("D:/test/scriptVar.js");
        Elements tt = doc.select("#productTitle");
        if(!tt.isEmpty()) {
            System.out.println(tt.text());
        }
        Prod p = new Prod();
        //将File对象f直接注入到js脚本中并可以作为全局变量使用
        engine.put("doc", doc);
//        engine.put("p", );
        Object title = engine.eval("var tt = doc.select(\"#productTitle\");tt.text();");
        Object salePrice = engine.eval("doc.select(\"#priceblock_ourprice11111\").text();");
        System.out.println(salePrice);
//        engine.eval("print(files.getPath());print(files.getName());print(files.isDirectory());files.createNewFile()");
    }

    class Prod {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    /**
     *  使用Script实现java接口
     * @throws Exception 
     */
    public void runnableImpl() throws Exception{
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        
        // String里定义一段JavaScript代码脚本
        String script = "function run() { print('run called'); }";
        // 执行这个脚本
        engine.eval(script);
        
        // 从脚本引擎中获取Runnable接口对象（实例）. 该接口方法由具有相匹配名称的脚本函数实现。
        Invocable inv = (Invocable) engine;
        // 在上面的脚本中，我们已经实现了Runnable接口的run()方法
        Runnable runnable = inv.getInterface(Runnable.class);
        
        // 启动一个线程运行上面的实现了runnable接口的script脚本
        Thread thread = new Thread(runnable);
        thread.start();
    }
    
    /**
     * 脚本引擎的多个scope
     * @throws Exception 
     */
    @Test
    public void multiScopes() throws Exception{
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        // 打印全局变量 "x"
        engine.put("x", "hello word!!");
        engine.eval("print(x);");
        // 上面的代码会打印"hello word！！"
        
        // 现在，传入另一个不同的script context
        ScriptContext context = new SimpleScriptContext();
        //新的Script context绑定ScriptContext的ENGINE_SCOPE
        Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        
        // 增加一个新变脸到新的范围 engineScope 中
        bindings.put("x", "word hello!!");
        // 执行同一个脚本 - 但这次传入一个不同的script context
        engine.eval("print(x);", bindings);
        engine.eval("print(x);");
    }
    
}
