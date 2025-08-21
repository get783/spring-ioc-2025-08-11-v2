package com.ll.framework.ioc;

import com.ll.domain.testPost.testPost.repository.TestPostRepository;
import com.ll.domain.testPost.testPost.service.TestFacadePostService;
import com.ll.domain.testPost.testPost.service.TestPostService;
import com.ll.framework.ioc.annotations.Component;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {
    private Reflections reflections;
    private Map<String, Object> beans;

    public ApplicationContext(String basePackage) {
        reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        beans = new HashMap<>();
    }

    public void init() {
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> component : components) {
            String className = component.getSimpleName();
            String classNameToCamelCase = className.substring(0, 1).toLowerCase() + className.substring(1);

            beans.put(classNameToCamelCase, genBean(classNameToCamelCase));
        }
    }

    public <T> T genBean(String beanName) {
        Object bean = beans.get(beanName);
        if (bean == null) {
            bean = switch (beanName) {
                case "testFacadePostService" -> new TestFacadePostService(
                        genBean("testPostService"),
                        genBean("testPostRepository")
                );
                case "testPostService" -> new TestPostService(
                        genBean("testPostRepository")
                );
                case "testPostRepository" -> new TestPostRepository();
                default -> null;
            };
            beans.put(beanName, bean);
        }
        return (T) bean;
    }
}
