package com.laquysoft.bernini.di

/**
 * Created by joaobiriba on 09/01/2018.
 */
object DaggerWrapper {

    private var mComponent: PolyComponent? = null

    val component: PolyComponent?
        get() {
            if (mComponent == null) {
                initComponent()
            }
            return mComponent
        }

    private fun initComponent() {
        mComponent = DaggerPolyComponent
                .builder()
                .build()
    }
}