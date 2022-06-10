package de.julianostarek.flow.persist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.julianostarek.flow.persist.FlowDatabase.Companion.DATABASE_VERSION
import de.julianostarek.flow.persist.converter.*
import de.julianostarek.flow.persist.dao.LocationDao
import de.julianostarek.flow.persist.dao.RouteDao
import de.julianostarek.flow.persist.model.*
import de.julianostarek.flow.profile.FlowProfile

@Database(
    entities = [
        LocationEntity::class,
        LineEntity::class,
        RouteEntity::class,
        ViaEntity::class,
        NetworkMapEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    ProductConverter::class,
    OffsetDateTimeConverter::class,
    UriConverter::class
)
abstract class FlowDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun routeDao(): RouteDao

    companion object : RoomDatabase.Callback() {
        private const val MAX_ROUTES = 20
        internal const val DATABASE_VERSION = 3
        private val instances: MutableMap<String, FlowDatabase> = HashMap()

        @JvmStatic
        fun getInstance(
            context: Context,
            profile: FlowProfile
        ): FlowDatabase {
            return instances.computeIfAbsent(profile.name) {
                val mappingConverter = ProductConverter(profile.productType)
                Room.databaseBuilder(context, FlowDatabase::class.java, profile.name)
                    .addTypeConverter(mappingConverter)
                    .addCallback(this)
                    .build()
            }
        }

        override fun onCreate(db: SupportSQLiteDatabase) {
            // db.execSQL("create trigger cleanup after insert on routes begin with redundant as (select id from routes order by last_queried desc limit $MAX_ROUTES, -1) delete from routes where id in redundant; end;")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            // do nothing
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            // do nothing
        }

    }

}