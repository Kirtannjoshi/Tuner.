package com.tuner.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ,\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\tH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\f\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ:\u0010\u0010\u001a\u0002H\u0011\"\u0004\b\u0000\u0010\u00112\u001c\u0010\u0012\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00110\u0014\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u00132\u0006\u0010\u0015\u001a\u0002H\u0011H\u0082@\u00a2\u0006\u0002\u0010\u0016J\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u0018\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006*\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/tuner/repository/StationRepository;", "", "api", "Lcom/tuner/api/RadioApi;", "(Lcom/tuner/api/RadioApi;)V", "getByGenre", "", "Lcom/tuner/model/Station;", "tag", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDiscoverStations", "countryCode", "language", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getNearby", "safeApiCall", "T", "block", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "default", "(Lkotlin/jvm/functions/Function1;Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchNetworks", "query", "filterValid", "app_debug"})
public final class StationRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.tuner.api.RadioApi api = null;
    
    @javax.inject.Inject()
    public StationRepository(@org.jetbrains.annotations.NotNull()
    com.tuner.api.RadioApi api) {
        super();
    }
    
    private final <T extends java.lang.Object>java.lang.Object safeApiCall(kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> block, T p1_772401952, kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
    
    private final java.util.List<com.tuner.model.Station> filterValid(java.util.List<com.tuner.model.Station> $this$filterValid) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDiscoverStations(@org.jetbrains.annotations.Nullable()
    java.lang.String countryCode, @org.jetbrains.annotations.Nullable()
    java.lang.String language, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchNetworks(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getByGenre(@org.jetbrains.annotations.NotNull()
    java.lang.String tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getNearby(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.tuner.model.Station>> $completion) {
        return null;
    }
}