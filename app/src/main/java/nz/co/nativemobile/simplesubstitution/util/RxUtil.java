package nz.co.nativemobile.simplesubstitution.util;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wadereweti on 10/19/15.
 */
public class RxUtil {
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <Uri> Observable.Transformer<Uri, Uri> applySchedulersUri() {
        return new Observable.Transformer<Uri, Uri>() {
            @Override
            public Observable<Uri> call(Observable<Uri> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable.Transformer<T, Void> toVoid() {
        return new Observable.Transformer<T, Void>() {
            @Override
            public Observable<Void> call(Observable<T> observable) {
                return observable.flatMap(new Func1<T, Observable<Void>>() {
                    @Override public Observable<Void> call(T value) {
                        return Observable.just(null);
                    }
                });
            }
        };
    }

    /**
     * Transforms an Observable<Iterable<T>> to an Observable<T>
     */
    public static <T> Observable.Transformer<Iterable<T>, T> flatten() {
        return new Observable.Transformer<Iterable<T>, T>() {
            @Override
            public Observable<T> call(Observable<Iterable<T>> observable) {
                return observable.flatMap(new Func1<Iterable<T>, Observable<T>>() {
                    @Override public Observable<T> call(Iterable<T> ts) {
                        return Observable.from(ts);
                    }
                });
            }
        };
    }
}
