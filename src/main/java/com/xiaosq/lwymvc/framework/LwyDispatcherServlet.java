package com.xiaosq.lwymvc.framework;

import com.xiaosq.lwymvc.framework.annotation.LwyController;
import com.xiaosq.lwymvc.framework.annotation.LwyRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LwyDispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private List<Handler> handlerMapping = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception, Msg :" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            //先取出来一个Handler，从HandlerMapping取
            Handler handler = getHandler(req);
            if (handler == null) {
                resp.getWriter().write("404 Not Found");
                return;
            }

            resp.getWriter().write("You are right=>" + req.getRequestURI());
        } catch (Exception e) {
            throw e;
        }
    }

    private Handler getHandler(HttpServletRequest req) {
        //循环handlerMapping
        if (handlerMapping.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.pattern.matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("servlet init");

        //IOC容器必须要先初始化
        //假装容器已启动
        LwyApplicationContext context = new LwyApplicationContext(config.getInitParameter(LOCATION));

        doLoadConfig(""); //config.getInitParameter("contextConfigLoacation"));

        doScanner("2423");

        doInstance();

        doAutowired();


        initHandlerMapping(context);
    }

    private void doLoadConfig(String contextConfiguration) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfiguration);

//        try {
//            contexConfig.load(is);
//        } catch(IOException e) {
//            e.printStackTrace();
//        } finally {
//            if(null != is) {
//                try {
//                    is.close();
//                } catch(IOException e) {
//                    e.printStackTrace();
//                } finally {
//
//                }
//            }
//        }
    }

    private void doScanner(String scanPackage) {
        this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("33", "22"));
    }

    private void doInstance() {

    }

    private void doAutowired() {

    }

    private void initHandlerMapping(LwyApplicationContext context) {
        Map<String, Object> ioc = context.getAll();
        if (ioc.isEmpty()) {
            return;
        }

        //只要是由Cotroller修饰类，里面方法全部找出来
        //而且这个方法上应该要加了RequestMaping注解，如果没加这个注解，这个方法是不能被外界来访问的
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(LwyController.class)) {
                continue;
            }


            String url = "";

            if (clazz.isAnnotationPresent(LwyRequestMapping.class)) {
                LwyRequestMapping requestMapping = clazz.getAnnotation(LwyRequestMapping.class);
                url = requestMapping.value();
            }

            //扫描Controller下面的所有的方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {

                if (!method.isAnnotationPresent(LwyRequestMapping.class)) {
                    continue;
                }

                LwyRequestMapping requestMapping = method.getAnnotation(LwyRequestMapping.class);
                String regex = (url + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);

                handlerMapping.add(new Handler(pattern, entry.getValue(), method));

                System.out.println("Mapping: " + regex + " " + method.toString());

            }
        }
    }


    /**
     * HandlerMapping的bean
     */
    private class Handler {
        protected Object controller;
        protected Method method;
        protected Pattern pattern;

        protected Handler(Pattern pattern, Object controller, Method method) {
            this.pattern = pattern;
            this.controller = controller;
            this.method = method;
        }
    }
}
