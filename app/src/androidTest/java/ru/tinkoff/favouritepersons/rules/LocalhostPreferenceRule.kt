package ru.tinkoff.favouritepersons.rules

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class LocalhostPreferenceRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                setStringPrefParam(spFileName, spParamUrlName, spParamAddress)
                base.evaluate()
                clearPreferences(spFileName)
            }

        }
    }

    companion object {
        private val spFileName = "demo_url"
        private val spParamUrlName = "url"
        private val spParamAddress = "http://localhost:5000/"
    }
}

private fun setStringPrefParam(prefName: String, param: String, value: String) {
    val pref = InstrumentationRegistry.getInstrumentation().targetContext.getSharedPreferences(
        prefName,
        Context.MODE_PRIVATE
    )
    val editor = pref.edit()
    editor.putString(param, value)
    editor.commit()
}

private fun clearPreferences(prefName: String) {
    val pref = InstrumentationRegistry.getInstrumentation().targetContext.getSharedPreferences(
        prefName,
        Context.MODE_PRIVATE
    )
    val editor = pref.edit()
    editor.clear()
    editor.commit()
}