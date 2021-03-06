package com.github.dmgcodevil.jmspy.proxy;

import com.github.dmgcodevil.jmspy.InvocationRecord;
import com.github.dmgcodevil.jmspy.proxy.wrappers.Wrapper;
import com.github.dmgcodevil.jmspy.reflection.Instantiator;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Creates proxy for a java beans.
 */
public class BeanProxyCreator extends AbstractProxyCreator implements ProxyCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanProxyCreator.class);

    BeanProxyCreator(Map<Class<?>, Wrapper> wrappers) {
        super(wrappers);
    }

    @Override
    <T> T createProxy(T target, String proxyId, InvocationRecord invocationRecord) throws Throwable {

        Class<T> proxyClass = ProxyFactory.getInstance().createProxyClass(target, proxyId, invocationRecord);
        if (proxyClass != null) {
            return Instantiator.getInstance().newInstance(proxyClass);
        }
        Optional<Wrapper> wrapperOpt = findWrapper(target.getClass());
        if (wrapperOpt.isPresent()) {
            Wrapper wr = wrapperOpt.get();
            Wrapper wrapper = wr.create(target);
            proxyClass = ProxyFactory.getInstance().createProxyClass(wrapper, proxyId, invocationRecord);
            return Instantiator.getInstance().newInstance(proxyClass);
        }

        LOGGER.error("failed create proxy for type: '{}'", target.getClass());
        return target;
    }

}
