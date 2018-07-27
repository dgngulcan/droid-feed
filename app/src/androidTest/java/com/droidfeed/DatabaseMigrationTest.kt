package com.droidfeed

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.Room
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.UiThreadTest
import com.droidfeed.data.db.MIGRATION_1_2
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private val TEST_DB_NAME = "dfTest.db"
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
            TEST_DB_NAME
        )

        val db = mSqliteTestDbHelper.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS `rss` (`bookmarked` INTEGER NOT NULL, `contentImage` TEXT NOT NULL, `link` TEXT NOT NULL, `pub_date` TEXT NOT NULL, `pub_date_timestamp` INTEGER NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, `content_raw` TEXT NOT NULL, `channel_title` TEXT NOT NULL, `channel_image_url` TEXT NOT NULL, `channel_link` TEXT NOT NULL, `content_image` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`link`))")
        db.close()
    }

    @After
    fun tearDown() {
        val db = mSqliteTestDbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS source")
        db.close()
    }

    @Test
    @UiThreadTest
    fun testMigrationFrom1To2_containsCorrectData() {
        // Create the database in version 1
        var db = testHelper.createDatabase(TEST_DB_NAME, 1)
        db.close()

        testHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            2,
            true,
            MIGRATION_1_2
        )

        val sources = getMigratedRoomDatabase().sourceDao().getSources()
    }

    private fun getMigratedRoomDatabase(): com.droidfeed.data.db.AppDatabase {
        val database = Room.databaseBuilder(
            InstrumentationRegistry.getTargetContext(),
            com.droidfeed.data.db.AppDatabase::class.java!!, TEST_DB_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        testHelper.closeWhenFinished(database)
        return database
    }
}