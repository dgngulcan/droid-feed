package com.droidfeed.ui.module.detail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import com.droidfeed.R
import com.droidfeed.data.model.Article
import com.droidfeed.databinding.ActivityArticleDetailBinding
import com.nytclient.ui.common.BaseActivity

/**
 * Created by Dogan Gulcan on 12/11/17.
 */
class ArticleDetailActivity : BaseActivity() {

    companion object {
        private val EXTRA_ARTICLE = "extra_article"

        fun getInstance(context: Context, article: Article): Intent {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE, article)
            return intent
        }
    }

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail)

        init()
    }

    private fun init() {

        val article = intent.getParcelableExtra<Article>(EXTRA_ARTICLE)
        val rawString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(article.rawContent.replace("<img.+?>", ""),
                    FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(article.rawContent.replace("<img.+?>", ""))
        }

        rawString.replace(Regex.fromLiteral("\\s*<a.*</a>\\s*"), "")

//        rawString.filterNot { it ->
//            it == 160.toChar() ||
//                    it == 65532.toChar() ||
//                    it == 65532.toChar()
//        }

        binding.txtView.text = rawString
//        binding.webView.loadDataWithBaseURL("",header + article.rawContent, "text/html", "UTF-8","")
//        binding.article = article

    }


}