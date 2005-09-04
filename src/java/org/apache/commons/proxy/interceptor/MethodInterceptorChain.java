/* $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.proxy.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.proxy.ObjectProvider;
import org.apache.commons.proxy.ProxyFactory;
import org.apache.commons.proxy.provider.AbstractObjectProvider;

/**
 * A <code>MethodInterceptorChain</code> assists with creating proxies which go through a series of
 * <code>MethodInterceptors</code>.
 *
 * @author James Carman
 * @version 1.0
 */
public class MethodInterceptorChain
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final MethodInterceptor[] interceptors;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public MethodInterceptorChain( MethodInterceptor... interceptors )
    {
        this.interceptors = interceptors;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    private Object createProxy( ProxyFactory proxyFactory, ClassLoader classLoader, Object terminus,
                                Class... proxyInterfaces )
    {
        Object currentTarget = terminus;
        for( int i = interceptors.length - 1; i >= 0; --i )
        {
            currentTarget = proxyFactory
                    .createInterceptingProxy( classLoader, currentTarget, interceptors[i], proxyInterfaces );
        }
        return currentTarget;
    }

    public ObjectProvider createProxyProvider( ProxyFactory proxyFactory, Object terminus, Class... proxyInterfaces )
    {
        return createProxyProvider( proxyFactory, Thread.currentThread().getContextClassLoader(), terminus,
                                    proxyInterfaces );
    }

    public ObjectProvider createProxyProvider( ProxyFactory proxyFactory, ClassLoader classLoader, Object terminus,
                                               Class... proxyInterfaces )
    {
        if( proxyInterfaces.length == 0 )
        {
            proxyInterfaces = terminus.getClass().getInterfaces();
        }
        return new ProxyObjectProvider( proxyFactory, classLoader, terminus, proxyInterfaces );
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private class ProxyObjectProvider extends AbstractObjectProvider
    {
        private final ClassLoader classLoader;
        private final Class[] proxyInterfaces;
        private final Object terminus;
        private final ProxyFactory proxyFactory;

        public ProxyObjectProvider( ProxyFactory proxyFactory, ClassLoader classLoader, Object terminus,
                                    Class[] proxyInterfaces )
        {
            this.classLoader = classLoader;
            this.proxyInterfaces = proxyInterfaces;
            this.terminus = terminus;
            this.proxyFactory = proxyFactory;
        }

        public Object getObject()
        {
            return createProxy( proxyFactory, classLoader, terminus, proxyInterfaces );
        }
    }
}

