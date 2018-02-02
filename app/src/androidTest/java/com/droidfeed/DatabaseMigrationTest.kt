package com.droidfeed

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.Room
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.droidfeed.data.db.MIGRATION_1_2
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


/**
 * Created by Dogan Gulcan on 1/27/18.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private val testDbNam = "dfTest.db"
    private lateinit var mSqliteTestDbHelper: SqliteTestDbOpenHelper

    @get:Rule
    val testHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        com.droidfeed.data.db.AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mSqliteTestDbHelper = SqliteTestDbOpenHelper(
            InstrumentationRegistry.getTargetContext(),
            testDbNam
        )
    }

    @After
    fun tearDown() {
        val db = mSqliteTestDbHelper.writableDatabase;
        db.execSQL("DROP TABLE IF EXISTS source")
        db.close()
    }


    @Test
    @UiThreadTest
    fun testMigrationFrom1To2_containsCorrectData() {
        // Create the database in version 1
        var db = testHelper.createDatabase(testDbNam, 1)
//        db.close()
        assertEquals(1, 1)

        testHelper.runMigrationsAndValidate(
            testDbNam,
            2,
            true,
            MIGRATION_1_2
        )

        val sources = getMigratedRoomDatabase().sourceDao().getSources().blockingObserve()
////        verify(observer).onChanged(sources)
//        sources?.let { assertEquals(true, sources.isNotEmpty()) }
//        assertEquals(1, 1)
    }


//    @Test
//    @UiThreadTest
//    fun testMig1To2() {
//        val db = testHelper.createDatabase(testDbNam, 1)
//
//        // Re-open the database with version 2 and provide
//        // MIGRATION_1_2 as the migration process.
//        testHelper.runMigrationsAndValidate(testDbNam, 2, true, MIGRATION_1_2);
////        val sources = getMigratedRoomDatabase().sourceDao().getSources().blockingObserve()
////        sources?.let { assertEquals(true, sources.isNotEmpty()) }
//
//        // MigrationTestHelper automatically verifies the schema changes,
//        // but you need to validate that the data was migrated properly.
//    }



    private fun getMigratedRoomDatabase(): com.droidfeed.data.db.AppDatabase {
        val database = Room.databaseBuilder(
            InstrumentationRegistry.getTargetContext(),
            com.droidfeed.data.db.AppDatabase::class.java!!, testDbNam
        )
            .addMigrations(MIGRATION_1_2)
            .build()
        // close the database and release any stream resources when the test finishes
        testHelper.closeWhenFinished(database)
        return database
    }

}