package com.tuner.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a2\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00040\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u000bH\u0007\u001a\b\u0010\f\u001a\u00020\u0004H\u0007\u001a<\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u000b2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00040\u000bH\u0007\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"NANO_NETWORKS", "", "Lcom/tuner/ui/NanoNetwork;", "HomeScreen", "", "viewModel", "Lcom/tuner/viewmodel/RadioViewModel;", "onPlayStation", "Lkotlin/Function1;", "Lcom/tuner/model/Station;", "onNavigateSettings", "Lkotlin/Function0;", "SkeletonCard", "StationCard", "station", "isActive", "", "isFavorite", "onClick", "onToggleFavorite", "app_debug"})
public final class HomeScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.tuner.ui.NanoNetwork> NANO_NETWORKS = null;
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    com.tuner.viewmodel.RadioViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.tuner.model.Station, kotlin.Unit> onPlayStation, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateSettings) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StationCard(@org.jetbrains.annotations.NotNull()
    com.tuner.model.Station station, boolean isActive, boolean isFavorite, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleFavorite) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SkeletonCard() {
    }
}