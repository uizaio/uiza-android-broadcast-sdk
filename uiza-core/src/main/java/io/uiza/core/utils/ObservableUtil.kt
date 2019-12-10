@file:JvmName("ObservableKt")

//@file:JvmMultifileClass

package io.uiza.core.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.uiza.core.helpers.ThreadHelper
import io.uiza.core.models.v3.ListV3Wrapper
import io.uiza.core.models.v3.ObjectV3Wrapper
import io.uiza.core.models.v5.ListWrapper

// process null data

data class Optional<T>(val value: T?)

fun <T> T?.asOptional() = Optional(this)

/**
 * Use {@link AndroidSchedulers.mainThread} and {@link Schedulers.newThread}
 */
//@JvmName("newThread")
fun <T> Observable<T>.newThread(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread()) // notify to main thread
        .subscribeOn(Schedulers.newThread()) // run in background
}

//@JvmName("ioThread")
fun <T> Observable<T>.io(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread()) // notify to main thread
        .subscribeOn(Schedulers.io()) // run in background
}

/**
 * Use {@link AndroidSchedulers.mainThread}
 * and {@link ThreadHelper}
 */
//@JvmName("customScheduler")
fun <T> Observable<T>.customThread(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(ThreadHelper.instance.scheduler)
}

/**
 * Use {@link Schedulers.io()} before subscribe
 */
//@JvmName("ioSubscribe")
@SchedulerSupport(SchedulerSupport.IO)
fun <T> Observable<T>.ioSubscribe(
    onNext: Consumer<in T>,
    onError: Consumer<in Throwable>? = null
): Disposable {
    return io().subscribe(onNext, onError)
}

fun <T> Observable<ListWrapper<T>>.getData(filter: (T) -> Boolean): Observable<Optional<List<T>>> {
    return this.map { m -> m.data?.filter(filter)?.asOptional() }
}

fun <T> Observable<ListWrapper<T>>.getData(): Observable<Optional<List<T>>> {
    return this.map { m -> m.data?.asOptional() }
}

// V3 API
fun <T> Observable<ListV3Wrapper<T>>.listV3Data(): Observable<Optional<List<T>>> {
    return this.map { m -> m.data?.asOptional() }
}

fun <T> Observable<ObjectV3Wrapper<T>>.getV3Data(): Observable<Optional<T>> {
    return this.map { m -> m.data?.asOptional() }
}
