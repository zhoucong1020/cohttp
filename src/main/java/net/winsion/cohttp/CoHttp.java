package net.winsion.cohttp;

import kotlin.coroutines.experimental.Continuation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import winsion.net.kotlinandroid.cohttp.CoHttpBuilder;
import winsion.net.kotlinandroid.cohttp.RequestBaseImpl;

import java.lang.reflect.*;
import java.util.List;

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
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Continuation continuation = (Continuation) args[args.length - 1];

                        Type[] genericParameterTypes = method.getGenericParameterTypes();
                        Class responseType = (Class) (
                                (WildcardType) (
                                        (ParameterizedType) genericParameterTypes[genericParameterTypes.length - 1]
                                ).getActualTypeArguments()[0]
                        ).getLowerBounds()[0];

                        Request request = new RequestBuilder(method).build(baseUrl, args);

                        for (ConverterFactory converterFactory : converterFactories) {
                            Converter converter = converterFactory.responseBodyConverter(responseType, method.getAnnotations());
                            if (converter != null) {
                                return base.coroutineRequest(request, converter, continuation);
                            }
                        }
                        throw new RuntimeException("no converter for type " + responseType.getName());
                    }
                });
    }
}
