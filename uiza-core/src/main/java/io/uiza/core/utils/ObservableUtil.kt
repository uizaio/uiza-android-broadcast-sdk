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
import io.uiza.core.models.v5.ListWrapper

/**
 * Use {@link AndroidSchedulers.mainThread} and {@link Schedulers.newThread}
 */
//@JvmName("newThread")
fun <T> Observable<T>.newThread(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.newThread())
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
 * Use {@link newThread} before subscribe
 */
//@JvmName("execSubscribe")
@SchedulerSupport(SchedulerSupport.NEW_THREAD)
fun <T> Observable<T>.execSubscribe(
    onNext: Consumer<in T>,
    onError: Consumer<in Throwable>? = null
): Disposable {
    return newThread().subscribe(onNext, onError)
}

fun <T> Observable<ListWrapper<T>>.getData(filter: (T) -> Boolean): Observable<List<T>> {
    return this.map { m -> m.data?.filter(filter) }
}

fun <T> Observable<ListWrapper<T>>.getData(): Observable<List<T>> {
    return this.map { m -> m.data }
}

