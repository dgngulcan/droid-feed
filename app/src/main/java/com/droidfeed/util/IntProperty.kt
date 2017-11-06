package com.droidfeed.util

import android.util.Property

/**
 * An implementation of [Property] to be used specifically with fields of
 * type
 * `int`. This type-specific subclass enables performance benefit by allowing
 * calls to a [set()][.set] function that takes the primitive
 * `int` type and avoids autoboxing and other overhead associated with the
 * `Integer` class.
 *
 * @param <T> The class on which the Property is declared.
 *
 * Adopted from https://github.com/nickbutcher/plaid/
 *
 * Created by Dogan Gulcan on 11/5/17.
 */
abstract class IntProperty<T>(name: String) : Property<T, Int>(Int::class.java, name) {

    /**
     * A type-specific override of the [.set] that is faster when dealing
     * with fields of type `int`.
     */
    abstract fun setValue(`object`: T, value: Int)

    override fun set(`object`: T, value: Int?) {
        setValue(`object`, value!!.toInt())
    }

}