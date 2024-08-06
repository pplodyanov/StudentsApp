package ru.tinkoff.favouritepersons

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

fun fileToString(
    path: String,
    context: Context = InstrumentationRegistry.getInstrumentation().targetContext
): String {
    return BufferedReader(
        InputStreamReader(
            context.assets.open(path),
            StandardCharsets.UTF_8
        )
    ).readText()
}
