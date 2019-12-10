package io.uiza.core.utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RxBinder {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public <T> void bind(Observable<T> observable,
                         final Consumer<T> onNext,
                         final Consumer<Exception> onError,
                         final Action onComplete) {
        compositeDisposable.add(
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<T>() {

                            @Override
                            public void onNext(T o) {
                                try {
                                    onNext.accept(o);
                                } catch (Exception e) {
                                    Timber.e(e, "Error calling onNext");
                                }
                            }

                            @Override
                            public void onError(Throwable thr) {
                                if (!(thr instanceof Exception)) {
                                    Exceptions.throwIfFatal(thr);
                                    return;
                                }
                                try {
                                    onError.accept((Exception) thr);
                                } catch (Exception e) {
                                    Timber.e(thr, "Error calling onError");
                                }
                            }

                            @Override
                            public void onComplete() {
                                try {
                                    onComplete.run();
                                } catch (Exception e) {
                                    Timber.e(e, "Error calling onComplete");
                                }
                            }
                        })
        );
    }

    public void clear() {
        compositeDisposable.clear();
    }
}