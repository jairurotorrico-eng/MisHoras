package com.jairo.calendariotrabajo

import android.app.Application
import com.jairo.calendariotrabajo.data.db.AppDatabase

class MisHorasApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
