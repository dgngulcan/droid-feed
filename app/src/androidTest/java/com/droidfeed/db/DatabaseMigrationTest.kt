package com.droidfeed.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.droidfeed.blockingObserve
import com.droidfeed.data.db.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private val dbName = "df_test.db"
    private lateinit var mSqliteTestDbHelper: SQLiteTestDbOpenHelper
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    var mMigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun setup() {
        mSqliteTestDbHelper = SQLiteTestDbOpenHelper(
            context,
            dbName
        )

        mSqliteTestDbHelper.writableDatabase.use { database ->
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `rss` " +
                        "(`bookmarked` INTEGER NOT NULL, `contentImage` " +
                        "TEXT NOT NULL, `link` TEXT NOT NULL, `pub_date` TEXT NOT NULL," +
                        " `pub_date_timestamp` INTEGER NOT NULL, `title` TEXT NOT NULL," +
                        " `author` TEXT NOT NULL, `content_raw` TEXT NOT NULL," +
                        " `channel_title` TEXT NOT NULL, `channel_image_url` TEXT NOT NULL," +
                        " `channel_link` TEXT NOT NULL, `content_image` TEXT NOT NULL, " +
                        "`content` TEXT NOT NULL, PRIMARY KEY(`link`))"
            )
        }
    }

    @After
    fun tearDown() {
        mSqliteTestDbHelper.writableDatabase.use { database ->
            database.execSQL("DROP TABLE IF EXISTS rss")
            database.execSQL("DROP TABLE IF EXISTS source")
        }
    }

    @Test
    fun migration_from_1_to_4_should_contain_correct_data() {
        createAndPopulateDBVersion1()

        val migrations = arrayOf(
            MIGRATION_1_2,
            MIGRATION_1_4,
            MIGRATION_2_3,
            MIGRATION_3_4
        )

        val posts = getMigrationValidatedRoomDatabase(migrations)
            .postDao()
            .getAllPostsLiveData()
            .blockingObserve() ?: emptyList()

        assertEquals("https://post.link.test", posts[0].link)
        assertEquals("https://channel.link.test", posts[0].channel.link)
        assertEquals(1, posts[0].bookmarked)
    }

    @Test
    fun migration_from_2_to_4_should_contain_correct_data() {
        createAndPopulateDBVersion2()

        val migrations = arrayOf(
            MIGRATION_1_4,
            MIGRATION_2_3,
            MIGRATION_3_4
        )
        val migratedDb = getMigrationValidatedRoomDatabase(migrations)

        mSqliteTestDbHelper.writableDatabase.use { database ->
            database.execSQL(
                "INSERT INTO `source` (id, url, name, is_active)" +
                        " VALUES ('3','https://some.url3','source name3', '1') ;"
            )
        }

        val sources = migratedDb.sourceDao().getSources().blockingObserve() ?: emptyList()
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

        val posts = migratedDb.postDao().getAllPostsLiveData().blockingObserve() ?: emptyList()
        assertEquals("https://post.link.test", posts[0].link)
        assertEquals("https://some.url2", posts[0].channel.link)
        assertEquals(2, posts[0].sourceId)
        assertEquals(1, posts[0].bookmarked)
    }

    @Test
    fun migration_from_3_to_4_should_contain_correct_data() {
        createAndPopulateDBVersion3()

        val migrations = arrayOf(
            MIGRATION_1_4,
            MIGRATION_3_4
        )

        val posts = getMigrationValidatedRoomDatabase(migrations)
            .postDao()
            .getAllPostsLiveData().blockingObserve() ?: emptyList()

        assertEquals("https://post.link.test", posts[0].link)
        assertEquals("https://channel.link.test", posts[0].channel.link)
        assertEquals(1, posts[0].bookmarked)
    }

    private fun createAndPopulateDBVersion1() {
        mMigrationTestHelper.createDatabase(dbName, 1).use { database ->
            database.execSQL(
                "INSERT INTO `rss`  (bookmarked, contentImage, link, pub_date, pub_date_timestamp, title, author, content_raw, channel_title, channel_image_url, channel_link, content_image, content)" +
                        " VALUES ('1', 'content image', 'https://post.link.test', 'somedate', '123132', 'title' ,'author', '', 'best channel title', 'http://image.url','https://channel.link.test', 'image', '' );"
            )
        }
    }

    private fun createAndPopulateDBVersion2() {
        mMigrationTestHelper.createDatabase(dbName, 2).use { database ->
            database.apply {
                execSQL("INSERT INTO `source` (url, name, is_active) VALUES ('https://some.url','source name', '0') ;")
                execSQL("INSERT INTO `source` (url, name, is_active) VALUES ('https://some.url2','source name2', '1') ;")
                execSQL("INSERT INTO `rss`  (bookmarked,content, content_image, channel_image_url,channel_title, content_raw, author, title, pub_date_timestamp, pub_date, contentImage, link,  channel_link) VALUES ('1','', '', '', '', '', 'author', 'ptitle', 'pub_date_timestamp', 'pub date', 'image', 'https://post.link.test', 'https://some.url2');")
            }
        }
    }

    private fun createAndPopulateDBVersion3() {
        mMigrationTestHelper.createDatabase(dbName, 3).use { database ->
            database.apply {
                execSQL("INSERT INTO `source` (id, url, name, is_active) VALUES ('0','https://some.url','source name', '0') ;")
                execSQL("INSERT INTO `rss`  (bookmarked,content, content_image, channel_image_url,channel_title, content_raw, author, title, pub_date_timestamp, pub_date, content_image, link,  channel_link) VALUES ('1','', '', '', '', '', 'author', 'ptitle', 'pub_date_timestamp', 'pub date', 'image', 'https://post.link.test', 'https://channel.link.test');")
            }
        }
    }

    private fun getMigrationValidatedRoomDatabase(migrations: Array<Migration>): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            dbName
        ).apply {
            addMigrations(*migrations)
            allowMainThreadQueries()
        }.run {
            build()
        }.also { database ->
            mMigrationTestHelper.closeWhenFinished(database)

            mMigrationTestHelper.runMigrationsAndValidate(
                dbName,
                3,
                true,
                *migrations
            )
        }
    }

}