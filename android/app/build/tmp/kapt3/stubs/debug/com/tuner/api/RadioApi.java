package com.tuner.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\r\bf\u0018\u00002\u00020\u0001JT\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0003\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\b\u0003\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010\f\u001a\u00020\u00062\b\b\u0003\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u000eJF\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0010\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010\f\u001a\u00020\u00062\b\b\u0003\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u0011JF\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0013\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010\f\u001a\u00020\u00062\b\b\u0003\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0015JF\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0017\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010\f\u001a\u00020\u00062\b\b\u0003\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u0011\u00a8\u0006\u0018"}, d2 = {"Lcom/tuner/api/RadioApi;", "", "advancedSearch", "", "Lcom/tuner/model/Station;", "countrycode", "", "language", "hidebroken", "", "limit", "", "order", "reverse", "(Ljava/lang/String;Ljava/lang/String;ZILjava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getByCountry", "countryCode", "(Ljava/lang/String;ZILjava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getByTag", "tag", "getTopStations", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchStations", "name", "app_debug"})
public abstract interface RadioApi {
    
    @retrofit2.http.GET(value = "stations/topvote/80")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTopStations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion);
    
    @retrofit2.http.GET(value = "stations/search")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object advancedSearch(@retrofit2.http.Query(value = "countrycode")
    @org.jetbrains.annotations.Nullable()
    java.lang.String countrycode, @retrofit2.http.Query(value = "language")
    @org.jetbrains.annotations.Nullable()
    java.lang.String language, @retrofit2.http.Query(value = "hidebroken")
    boolean hidebroken, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "order")
    @org.jetbrains.annotations.NotNull()
    java.lang.String order, @retrofit2.http.Query(value = "reverse")
    boolean reverse, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion);
    
    @retrofit2.http.GET(value = "stations/byname/{name}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchStations(@retrofit2.http.Path(value = "name")
    @org.jetbrains.annotations.NotNull()
    java.lang.String name, @retrofit2.http.Query(value = "hidebroken")
    boolean hidebroken, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "order")
    @org.jetbrains.annotations.NotNull()
    java.lang.String order, @retrofit2.http.Query(value = "reverse")
    boolean reverse, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion);
    
    @retrofit2.http.GET(value = "stations/bytagexact/{tag}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByTag(@retrofit2.http.Path(value = "tag")
    @org.jetbrains.annotations.NotNull()
    java.lang.String tag, @retrofit2.http.Query(value = "hidebroken")
    boolean hidebroken, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "order")
    @org.jetbrains.annotations.NotNull()
    java.lang.String order, @retrofit2.http.Query(value = "reverse")
    boolean reverse, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion);
    
    @retrofit2.http.GET(value = "stations/bycountrycodeexact/{code}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByCountry(@retrofit2.http.Path(value = "code")
    @org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @retrofit2.http.Query(value = "hidebroken")
    boolean hidebroken, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "order")
    @org.jetbrains.annotations.NotNull()
    java.lang.String order, @retrofit2.http.Query(value = "reverse")
    boolean reverse, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}