package com.tuner;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = TunerApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface TunerApplication_GeneratedInjector {
  void injectTunerApplication(TunerApplication tunerApplication);
}
