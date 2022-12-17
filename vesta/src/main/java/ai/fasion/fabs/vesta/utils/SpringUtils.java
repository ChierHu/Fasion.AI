package ai.fasion.fabs.vesta.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Function:spring工具类 方便在非spring管理环境中获取bean
 *
 * @author miluo
 * Date: 2021/3/10 10:44
 * @since JDK 1.8
 */
@Component
public final class SpringUtils implements BeanFactoryPostProcessor {
    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }


    public static boolean judge(ApplicationContext applicationContext, String keywords) {
        Environment evn = applicationContext.getEnvironment();
        String[] activeProfiles = evn.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            if (keywords.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }
}
