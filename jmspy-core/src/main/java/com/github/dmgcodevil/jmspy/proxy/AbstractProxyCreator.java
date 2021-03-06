package com.github.dmgcodevil.jmspy.proxy;

import com.github.dmgcodevil.jmspy.InvocationRecord;
import com.github.dmgcodevil.jmspy.exception.ProxyCreationException;
import com.github.dmgcodevil.jmspy.proxy.wrappers.Wrapper;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * Basic implementation of {@link ProxyCreator}.
 * <p/>
 * @author dmgcodevil
 */
public abstract class AbstractProxyCreator implements ProxyCreator {

    protected Map<Class<?>, Wrapper> wrappers;

    protected AbstractProxyCreator(Map<Class<?>, Wrapper> wrappers) {
        this.wrappers = wrappers;
    }

    @Override
    public <T> T create(T target, String proxyId, InvocationRecord invocationRecord) throws ProxyCreationException {
        if (target == null) {
            return null;
        }
        try {
            return createProxy(target, proxyId, invocationRecord);
        } catch (Throwable throwable) {
            throw new ProxyCreationException("type: " + target.getClass(), throwable);
        }
    }

    /**
     * This abstract method is a part of template method
     * {@link AbstractProxyCreator#createProxy(Object, String, com.github.dmgcodevil.jmspy.InvocationRecord)}
     * and used to create proxies.
     *
     * @param target the target object
     * @return proxy
     * @throws Throwable in a case of any errors
     */
    abstract <T> T createProxy(T target, String proxyId, InvocationRecord invocationRecord) throws Throwable;

    /**
     * Tries find a wrapper for the given type.
     *
     * @param type the type to find certain wrapper
     * @return holder of result object. to check whether holder contains a (non-null)
     * instance use {@link com.google.common.base.Optional#isPresent()}
     */
    Optional<Wrapper> findWrapper(Class<?> type) {
        Optional<Wrapper> optional = Optional.absent();
        for (Map.Entry<Class<?>, Wrapper> entry : wrappers.entrySet()) {
            if (entry.getKey().equals(type)) {
                optional = Optional.of(entry.getValue());
                break;
            }
        }

        // this iteration is required because if we deal with nested
        // or anonymous classes that are subclasses of some public classes or interfaces then condition based
        // on equals() between two classes isn't enough
        // for example unmodifiable set returns iterator that is anonymous class in unmodifiable
        // list and has next class name in runtime 'java.util.Collections$UnmodifiableCollection$1'
        // thus we need to use isAssignableFrom() to find wrapper

        if (!optional.isPresent()) {
            for (Map.Entry<Class<?>, Wrapper> entry : wrappers.entrySet()) {
                if (entry.getKey().isAssignableFrom(type)) {
                    optional = Optional.of(entry.getValue());
                    break;
                }
            }
        }
        return optional;
    }

}
