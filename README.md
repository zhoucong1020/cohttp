# CoHttp
[![Current](https://img.shields.io/badge/current-0.16-blue.svg)](https://bintray.com/winsion/maven/CoHttp)
[![Building](https://img.shields.io/badge/building-passing-brightgreen.svg)]()
[![Coverage](https://img.shields.io/badge/coverage-90%25-green.svg)]()

Type-safe HTTP client use `coroutine` for Android and Kotlin by WINSION.

CoHttp API is very close to [Retrofit](https://github.com/square/retrofit), so Retrofit users can easy to accept it.

## Usage

    repositories {
        maven {
            url  "https://winsion.bintray.com/maven" 
        }
    }
        
    dependencies {
        compile 'net.winsion:cohttp:0.16'
    }
    
You should add another dependency in android project:

    compile "org.jetbrains.kotlinx:kotlinx-coroutines-android:0.18"

Now you can make request like this

    launch(UI) {
        val someInterface = coHttp.create(SomeInterface::class.java)
        val result = someInterface.getSomeString()
        tv_hello.text = result
    }

## Introduction
CoHttp turns your HTTP API into a kotlin interface and all your API should declared to `suspend function` in kotlin.

    interface TestInterface {
        @FormUrlEncoded
        @POST("/w/index.php")
        suspend fun post(@Field("search") search: String): ResponseBody
    }

The `CoHttp` class generates an implementation of the `TestInterface` interface.

    val cohttp = CoHttp.builder()
            .baseUrl("https://en.wikipedia.org")
            .build()
            
    cohttp.create(TestInterface::class.java).post("Jurassic Park")

# Features
## 1.1 simple request
Every method must have an HTTP annotation that provides the request method and relative URL. There are five built-in annotations: `GET`, `POST`, `PUT`, `DELETE`, and `HEAD`. The relative URL of the resource is specified in the annotation.

    @GET("users/list")

## 1.2 static request parameter
You can also specify query parameters in the URL.

    @GET("users/list?sort=desc")

## 1.3 path parameter
A request URL can be updated dynamically using replacement blocks and parameters on the method. A replacement block is an alphanumeric string surrounded by { and }. A corresponding parameter must be annotated with `@Path` using the same string.

    @GET("group/{id}/users")
    String groupList(@Path("id") int groupId);

## 1.4 query parameter
Query parameters can also be added.

    @GET("group/{id}/users")
    List<User> groupList(@Path("id") int groupId, @Query("sort") String sort);

## 1.5 query map
For complex query parameter combinations a `Map` can be used.

    @GET("group/{id}/users")
    String groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);

## 2.1 request body (*)
An object can be specified for use as an HTTP request body with the `@Body` annotation.

    @POST("users/new")
    User createUser(@Body User user);

The object will also be converted using a `converter` specified on the `CoHttp` instance. If no converter is added, only `RequestBody` can be used.

## 2.2 post form encoded
Methods can also be declared to send form-encoded and multipart data.

Form-encoded data is sent when `@FormUrlEncoded` is present on the method. Each key-value pair is annotated with `@Field` containing the name and the object providing the value.

    @FormUrlEncoded
    @POST("user/edit")
    String updateUser(@Field("first_name") String first, @Field("last_name") String last);
    
## 2.3 multipart
Multipart requests are used when `@Multipart` is present on the method. Parts are declared using the `@Part` annotation.

    @Multipart
    @PUT("user/photo")
    User updateUser(
        @Part("name=\"file\";filename=\"file.jpg\"") file: RequestBody,
        @Part("description") RequestBody description
    );

## 3.1 static header (*)
You can set static headers for a method using the @Headers annotation.

    @Headers("Cache-Control: max-age=640000")
    @GET("widget/list")
    List<Widget> widgetList();
    
    @Headers({
        "Accept: application/vnd.github.v3.full+json",
        "User-Agent: Retrofit-Sample-App"
    })
    @GET("users/{username}")
    User getUser(@Path("username") String username);
    
Note that headers do not overwrite each other. All headers with the same name will be included in the request.

## 3.2 dynamic header (*)
A request Header can be updated dynamically using the `@Header` annotation. A corresponding parameter must be provided to the `@Header`. If the value is null, the header will be omitted. Otherwise, `toString` will be called on the value, and the result used.

    @GET("user")
    User getUser(@Header("Authorization") String authorization)
    
Headers that need to be added to every request can be specified using an `OkHttp` interceptor.

## 4.1 converter
    val cohttp = CoHttp.builder()
            .baseUrl("https://en.wikipedia.org")
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory())
            .build()
            
    class GsonConverterFactory : ConverterFactory {
        val gson: Gson = Gson()
        
        override fun responseBodyConverter(type: Type, annotations: Array<Annotation>): Converter<ResponseBody, *>? {
            return object : Converter<ResponseBody, Any> {
                override fun convert(value: ResponseBody): Any {
                    return gson.fromJson(value.string(), type)
                }
            }
        }
        
        override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>): Converter<*, RequestBody>? {
            return null
        }
    }