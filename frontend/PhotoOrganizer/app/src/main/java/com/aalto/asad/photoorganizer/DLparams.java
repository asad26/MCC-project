package com.aalto.asad.photoorganizer;

import java.io.File;

/**
 * Created by Juuso on 10.12.2017.
 */

public final class DLparams {
    String url;
    File dest;

    DLparams(String url, File dest) {
        this.url = url;
        this.dest = dest;
    }
}
