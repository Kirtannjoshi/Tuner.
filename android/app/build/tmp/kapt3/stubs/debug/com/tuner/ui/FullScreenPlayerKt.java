package com.tuner.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u008c\u0001\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u0011H\u0007\u001a\u0018\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u0006H\u0007\u001a^\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u000fH\u0007\u001a\u001e\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u00042\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00010\u000fH\u0007\u001a\u0018\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001d2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0006H\u0007\u00a8\u0006 "}, d2 = {"FullScreenPlayer", "", "stations", "", "Lcom/tuner/model/Station;", "initialStationId", "", "isPlaying", "", "favorites", "", "audioOutput", "sessionDuration", "", "onPlayPause", "Lkotlin/Function0;", "onToggleFavorite", "Lkotlin/Function1;", "onMinimize", "onStationSelected", "InfoChip", "label", "value", "PlayerPage", "station", "isFavorite", "StationDetailSheet", "onDismiss", "rememberDominantColor", "Landroidx/compose/runtime/State;", "Landroidx/compose/ui/graphics/Color;", "imageUrl", "app_debug"})
public final class FullScreenPlayerKt {
    
    @androidx.compose.runtime.Composable()
    @org.jetbrains.annotations.NotNull()
    public static final androidx.compose.runtime.State<androidx.compose.ui.graphics.Color> rememberDominantColor(@org.jetbrains.annotations.Nullable()
    java.lang.String imageUrl) {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.ExperimentalFoundationApi.class})
    @androidx.compose.runtime.Composable()
    public static final void FullScreenPlayer(@org.jetbrains.annotations.NotNull()
    java.util.List<com.tuner.model.Station> stations, @org.jetbrains.annotations.NotNull()
    java.lang.String initialStationId, boolean isPlaying, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> favorites, @org.jetbrains.annotations.NotNull()
    java.lang.String audioOutput, long sessionDuration, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPlayPause, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onToggleFavorite, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onMinimize, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.tuner.model.Station, kotlin.Unit> onStationSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PlayerPage(@org.jetbrains.annotations.NotNull()
    com.tuner.model.Station station, boolean isPlaying, boolean isFavorite, @org.jetbrains.annotations.NotNull()
    java.lang.String audioOutput, long sessionDuration, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPlayPause, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleFavorite, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onMinimize) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void StationDetailSheet(@org.jetbrains.annotations.NotNull()
    com.tuner.model.Station station, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void InfoChip(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
}