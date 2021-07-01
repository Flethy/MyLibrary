package com.flethy.mylibrary;

import android.app.Application;
import com.flethy.mylibrary.di.AppComponent

class BookApp: Application() {

    val myComponent: AppComponent by lazy { AppComponent(this) }

}