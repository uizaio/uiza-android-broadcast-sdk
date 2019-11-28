//@file:JvmName("ObservableKt")

//@file:JvmMultifileClass

package io.uiza.core.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

object ObservableUtil {
    @JvmStatic
    fun <T> subscribe(
        observable: Observable<T>,
        onNext: Consumer<in T>?,
        onError: Consumer<in Throwable?>?
    ): Disposable {
        //TODO maybe in some cases we don't need to check internet connection
        return observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, onError)
    }
}



