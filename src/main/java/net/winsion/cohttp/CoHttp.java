package net.winsion.cohttp;

import okhttp3.OkHttpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class CoHttp {
    private String baseUrl;
    private RequestBaseImpl base;
    private List<ConverterFactory> converterFactories;

    public static CoHttpBuilder builder() {
        return new CoHttpBuilder();
    }

    public CoHttp(String baseUrl, OkHttpClient client, List<ConverterFactory> converterFactories) {
        this.baseUrl = baseUrl;
        this.base = new RequestBaseImpl(client);
        this.converterFactories = converterFactories;
    }

    public <T> T create(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {
                    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (!serviceMethodCache.containsKey(method)) {
                            synchronized (serviceMethodCache) {
                                //double check
                                if (!serviceMethodCache.containsKey(method)) {
                                    serviceMethodCache.put(method, new ServiceMethod(method, baseUrl, base, converterFactories));
                                }
                            }
                        }

                        ServiceMethod serviceMethod = serviceMethodCache.get(method);
                        return serviceMethod.invoke(args);
                    }
                });
    }
}
