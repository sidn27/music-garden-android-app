package com.musicgarden.android.lib;

import com.musicgarden.android.responses.HomeResponse;

import java.lang.reflect.Type;

/**
 * Created by Necra on 24-12-2017.
 */

public abstract class Callback {

    final Class<?> typeParameterClass;

    public Callback(Class<?> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public Class<?> getTypeParameterClass() {
        return typeParameterClass;
    }

    public abstract void call(Object response);

}
