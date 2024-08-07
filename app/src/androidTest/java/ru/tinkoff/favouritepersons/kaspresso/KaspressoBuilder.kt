package ru.tinkoff.favouritepersons.kaspresso

import com.kaspersky.components.alluresupport.interceptors.step.AllureMapperStepInterceptor
import com.kaspersky.kaspresso.interceptors.watcher.testcase.impl.views.DumpViewsInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.params.FlakySafetyParams

val kaspressoBuilder = Kaspresso.Builder.simple(
    customize = {
        flakySafetyParams = FlakySafetyParams.custom(timeoutMs = 10_000, intervalMs = 100)
        beforeEachTest {
            //code before each test
            testLogger.i("Before")
        }
        afterEachTest {
            testLogger.i("After")
        }
    }

)
    .apply {
        testRunWatcherInterceptors.addAll(listOf(DumpViewsInterceptor(viewHierarchyDumper)))
        stepWatcherInterceptors.addAll(listOf(AllureMapperStepInterceptor()))
    }