package com.tuner.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u0000 \'2\u00020\u0001:\u0002\'(B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\u0018\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u0015\u001a\u00020\bH\u0002J\u0012\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010 \u001a\u00020\u0019H\u0016J\b\u0010!\u001a\u00020\u0019H\u0016J\"\u0010\"\u001a\u00020\u000b2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010#\u001a\u00020\u000b2\u0006\u0010$\u001a\u00020\u000bH\u0016J\b\u0010%\u001a\u00020\u0019H\u0002J\u0010\u0010&\u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u0012\u0010\u0003\u001a\u00060\u0004R\u00020\u0000X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006)"}, d2 = {"Lcom/tuner/service/RadioService;", "Landroid/app/Service;", "()V", "binder", "Lcom/tuner/service/RadioService$LocalBinder;", "currentBitmap", "Landroid/graphics/Bitmap;", "currentFavicon", "", "currentName", "currentNotificationColor", "", "currentTags", "mediaSession", "Landroid/support/v4/media/session/MediaSessionCompat;", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "playerListener", "Landroidx/media3/common/Player$Listener;", "buildNotification", "Landroid/app/Notification;", "name", "playing", "", "createNotificationChannel", "", "loadAndPlay", "url", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onStartCommand", "flags", "startId", "requestFocusAndPlay", "updateNotification", "Companion", "LocalBinder", "app_debug"})
public final class RadioService extends android.app.Service {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_STREAM_URL = "stream_url";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_STATION_NAME = "station_name";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_PLAY = "com.tuner.ACTION_PLAY";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_TOGGLE = "com.tuner.ACTION_TOGGLE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_STOP = "com.tuner.ACTION_STOP";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_NEXT = "com.tuner.ACTION_NEXT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_PREV = "com.tuner.ACTION_PREV";
    @org.jetbrains.annotations.Nullable()
    private static kotlin.jvm.functions.Function0<kotlin.Unit> onNextRequested;
    @org.jetbrains.annotations.Nullable()
    private static kotlin.jvm.functions.Function0<kotlin.Unit> onPrevRequested;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isPlaying = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isPlaying = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    @org.jetbrains.annotations.NotNull()
    private final com.tuner.service.RadioService.LocalBinder binder = null;
    private androidx.media3.exoplayer.ExoPlayer player;
    private android.support.v4.media.session.MediaSessionCompat mediaSession;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentName = "Tuner";
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentFavicon;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentTags = "LIVE RADIO";
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap currentBitmap;
    private int currentNotificationColor = -1292135;
    @org.jetbrains.annotations.NotNull()
    private final androidx.media3.common.Player.Listener playerListener = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.tuner.service.RadioService.Companion Companion = null;
    
    public RadioService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void loadAndPlay(java.lang.String url, java.lang.String name) {
    }
    
    private final void requestFocusAndPlay() {
    }
    
    private final void updateNotification(boolean playing) {
    }
    
    private final android.app.Notification buildNotification(java.lang.String name, boolean playing) {
        return null;
    }
    
    private final void createNotificationChannel() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00040\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00040\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\"\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0016\u0018\u00010\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\"\u0010\u001b\u001a\n\u0012\u0004\u0012\u00020\u0016\u0018\u00010\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0018\"\u0004\b\u001d\u0010\u001a\u00a8\u0006\u001e"}, d2 = {"Lcom/tuner/service/RadioService$Companion;", "", "()V", "ACTION_NEXT", "", "ACTION_PLAY", "ACTION_PREV", "ACTION_STOP", "ACTION_TOGGLE", "EXTRA_STATION_NAME", "EXTRA_STREAM_URL", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "_isPlaying", "", "error", "Lkotlinx/coroutines/flow/StateFlow;", "getError", "()Lkotlinx/coroutines/flow/StateFlow;", "isPlaying", "onNextRequested", "Lkotlin/Function0;", "", "getOnNextRequested", "()Lkotlin/jvm/functions/Function0;", "setOnNextRequested", "(Lkotlin/jvm/functions/Function0;)V", "onPrevRequested", "getOnPrevRequested", "setOnPrevRequested", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnNextRequested() {
            return null;
        }
        
        public final void setOnNextRequested(@org.jetbrains.annotations.Nullable()
        kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnPrevRequested() {
            return null;
        }
        
        public final void setOnPrevRequested(@org.jetbrains.annotations.Nullable()
        kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isPlaying() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/tuner/service/RadioService$LocalBinder;", "Landroid/os/Binder;", "(Lcom/tuner/service/RadioService;)V", "getService", "Lcom/tuner/service/RadioService;", "app_debug"})
    public final class LocalBinder extends android.os.Binder {
        
        public LocalBinder() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.tuner.service.RadioService getService() {
            return null;
        }
    }
}