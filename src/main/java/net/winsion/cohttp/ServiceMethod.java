package net.winsion.cohttp;

import kotlin.coroutines.experimental.Continuation;
import okhttp3.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

class ServiceMethod {
    private Method method;

    private String baseUrl;
    private RequestBaseImpl base;
    private List<ConverterFactory> converterFactories;

    private Class responseType;

    ServiceMethod(Method method, String baseUrl, RequestBaseImpl base, List<ConverterFactory> converterFactories) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.base = base;
        this.converterFactories = converterFactories;

        if (!method.getParameterTypes()[method.getParameterTypes().length - 1].equals(Continuation.class)) {
            throw new RuntimeException("not a suspend func " + method.getName());
        }

        resolveResponseType();
    }

    @SuppressWarnings("unchecked")
    Object invoke(Object[] args) {
        Request request = new RequestBuilder(method).build(baseUrl, args, converterFactories);

        Continuation continuation = (Continuation) args[args.length - 1];
        return base.coroutineRequest(request, findConverter(responseType, method.getAnnotations()), continuation);
    }

    private void resolveResponseType() {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        responseType = (Class) (
                (WildcardType) (
                        (ParameterizedType) genericParameterTypes[genericParameterTypes.length - 1]
                ).getActualTypeArguments()[0]
        ).getLowerBounds()[0];
    }

    private Converter findConverter(Class type, Annotation[] annotations) {
        for (ConverterFactory converterFactory : converterFactories) {
            Converter converter = converterFactory.responseBodyConverter(type, annotations);
            if (converter != null) {
                return converter;
            }
        }
        throw new RuntimeException("no converter for type " + responseType.getName());
    }
}
