package com.example.razvan.edittextinrecyclerview.retrofit;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

public final class RetrofitExceptionHelper {

    private RetrofitExceptionHelper() {
    }

    public static RetrofitException httpError(String url, Response response, Retrofit retrofit) {
        String message = String.format("%s, %s", response.code(), response.message());
        return new RetrofitException(message, url, response, RetrofitException.Kind.HTTP, null, retrofit);
    }

    public static RetrofitException networkError(IOException exception) {
        return new RetrofitException(exception.getMessage(), null, null, RetrofitException.Kind.NETWORK, exception, null);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, RetrofitException.Kind.UNEXPECTED, exception, null);
    }
}
