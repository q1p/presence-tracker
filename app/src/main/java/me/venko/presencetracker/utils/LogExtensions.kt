package me.venko.presencetracker.utils

import android.util.Log

/**
 * @author Victor Kosenko
 */
object LogUtils {

    var loggingLevel = 0
}

private val Any.tag: String
    get() = this::class.java.simpleName

fun Any.logd(message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.DEBUG) {
        Log.d(tag, message.toStringSafe())
    }
}

fun Any.logd(cause: Throwable, message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.DEBUG) {
        Log.d(tag, message.toStringSafe(), cause)
    }
}

fun Any.logi(message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.INFO) {
        Log.i(tag, message.toStringSafe())
    }
}

fun Any.logi(cause: Throwable, message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.INFO) {
        Log.i(tag, message.toStringSafe(), cause)
    }
}

fun Any.logv(message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.VERBOSE) {
        Log.v(tag, message.toStringSafe())
    }
}

fun Any.logv(cause: Throwable, message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.VERBOSE) {
        Log.v(tag, message.toStringSafe(), cause)
    }
}

fun Any.logw(message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.WARN) {
        Log.w(tag, message.toStringSafe())
    }
}

fun Any.logw(cause: Throwable, message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.WARN) {
        Log.w(tag, message.toStringSafe(), cause)
    }
}

fun Any.loge(message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.ERROR) {
        Log.e(tag, message.toStringSafe())
    }
}

fun Any.loge(cause: Throwable, message: () -> Any?) {
    if (LogUtils.loggingLevel <= Log.ERROR) {
        Log.e(tag, message.toStringSafe(), cause)
    }
}

internal inline fun (() -> Any?).toStringSafe(): String {
    return try {
        invoke().toString()
    } catch (e: Exception) {
        "Log message invocation failed: $e"
    }
}