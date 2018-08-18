package com.droidfeed

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.Room
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.data.db.MIGRATION_1_2
import com.droidfeed.data.db.MIGRATION_2_3
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private val TEST_DB_NAME = "df_test.db"
    private lateinit var mSqliteTestDbHelper: SqliteTestDbOpenHelper

    @get:Rule
    @JvmField
    var instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    var mMigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        // create first version of DB
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
        db.execSQL("DROP TABLE IF EXISTS rss")
        db.execSQL("DROP TABLE IF EXISTS source")
        db.close()
    }

    @Test
    fun testMigrationFrom1To3_containsCorrectData() {
        mMigrationTestHelper.createDatabase(TEST_DB_NAME, 1).apply {
            execSQL(
                "INSERT INTO `rss`  (bookmarked, contentImage, link, pub_date, pub_date_timestamp, title, author, content_raw, channel_title, channel_image_url, channel_link, content_image, content)" +
                    " VALUES ('1', 'content image', 'https://post.link.test', 'somedate', '123132', 'title' ,'author', '', 'best channel title', 'http://image.url','https://channel.link.test', 'image', '' );"
            )
            close()
        }

        mMigrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME, 3,
            true,
            MIGRATION_1_2,
            MIGRATION_2_3
        )
        val posts = getMigratedRoomDatabase().postDao().getAllPostsLiveData().blockingObserve() ?: emptyList()

        assertEquals("https://post.link.test", posts[0].link)
        assertEquals("https://channel.link.test", posts[0].channel.link)
        assertEquals(1, posts[0].bookmarked)
    }

    @Test
    fun testMigrationFrom2To3_correctData() {
        mMigrationTestHelper.createDatabase(TEST_DB_NAME, 2).apply {
            execSQL("INSERT INTO `source` (url, name, is_active) VALUES ('https://some.url','source name', '0') ;")
            execSQL("INSERT INTO `source` (url, name, is_active) VALUES ('https://some.url2','source name2', '1') ;")
            execSQL("INSERT INTO `rss`  (bookmarked,content, content_image, channel_image_url,channel_title, content_raw, author, title, pub_date_timestamp, pub_date, contentImage, link,  channel_link) VALUES ('1','', '', '', '', '', 'author', 'ptitle', 'pub_date_timestamp', 'pub date', 'image', 'https://post.link.test', 'https://some.url2');")
            close()
        }

        mMigrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME, 3,
            true,
            MIGRATION_1_2,
            MIGRATION_2_3
        )

        mSqliteTestDbHelper.writableDatabase.apply {
            execSQL("INSERT INTO `source` (id, url, name, is_active) VALUES ('3','https://some.url3','source name3', '1') ;")
            close()
        }

        val sources = getMigratedRoomDatabase().sourceDao().getSources().blockingObserve() ?: emptyList()

        assertTrue((sources).size == 3)

        assertEquals("https://some.url", sources[0].url)
        assertEquals("source name", sources[0].name)
        assertEquals(false, sources[0].isActive)
        assertEquals(1, sources[0].id)

        assertEquals("https://some.url2", sources[1].url)
        assertEquals("source name2", sources[1].name)
        assertEquals(true, sources[1].isActive)
        assertEquals(2, sources[1].id)

        assertEquals("https://some.url3", sources[2].url)
        assertEquals("source name3", sources[2].name)
        assertEquals(true, sources[2].isActive)
        assertEquals(3, sources[2].id)

        val posts = getMigratedRoomDatabase().postDao().getAllPostsLiveData().blockingObserve() ?: emptyList()
        assertEquals("https://post.link.test", posts[0].link)
        assertEquals("https://some.url2", posts[0].channel.link)
        assertEquals(2, posts[0].sourceId)
        assertEquals(1, posts[0].bookmarked)
    }

    private fun getMigratedRoomDatabase(): com.droidfeed.data.db.AppDatabase {
        val database = Room.databaseBuilder(
            InstrumentationRegistry.getTargetContext(),
            com.droidfeed.data.db.AppDatabase::class.java,
            TEST_DB_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()

        mMigrationTestHelper.closeWhenFinished(database)
        return database
    }
}