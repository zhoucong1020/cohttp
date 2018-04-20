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

    private boolean isSuspendFunction = true;
    private Type responseType;

    ServiceMethod(Method method, String baseUrl, RequestBaseImpl base, List<ConverterFactory> converterFactories) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.base = base;
        this.converterFactories = converterFactories;

        if (method.getParameterTypes().length == 0 || !method.getParameterTypes()[method.getParameterTypes().length - 1].equals(Continuation.class)) {
            isSuspendFunction = false;
        }

        resolveResponseType();
    }

    @SuppressWarnings("unchecked")
    Object invoke(Object[] args) {
        Request request = new RequestBuilder(method).build(baseUrl, args, converterFactories);

        if (isSuspendFunction) {
            Continuation continuation = (Continuation) args[args.length - 1];
            return base.coroutineRequest(request, findConverter(responseType, method.getAnnotations()), continuation);
        } else {
            return new CancelableRequest(request, findConverter(responseType, method.getAnnotations()), base);
        }
    }

    private void resolveResponseType() {
        if (isSuspendFunction) {
            Type[] genericParameterTypes = method.getGenericParameterTypes();

            ParameterizedType parameterizedType = (ParameterizedType) genericParameterTypes[genericParameterTypes.length - 1];
            WildcardType wildcardType = (WildcardType) (parameterizedType).getActualTypeArguments()[0];
            responseType = wildcardType.getLowerBounds()[0];
        } else {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            if (parameterizedType.getActualTypeArguments()[0] instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) (parameterizedType).getActualTypeArguments()[0];
                responseType = wildcardType.getLowerBounds()[0];
            } else {
                responseType = parameterizedType.getActualTypeArguments()[0];
            }
        }
    }

    private Converter findConverter(Type type, Annotation[] annotations) {
        for (ConverterFactory converterFactory : converterFactories) {
            Converter converter = converterFactory.responseBodyConverter(type, annotations);
            if (converter != null) {
                return converter;
            }
        }
        throw new RuntimeException("no converter for type " + responseType.toString());
    }
}
