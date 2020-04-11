package com.droidfeed.ui.module.onboard

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.droidfeed.R
import com.droidfeed.data.repo.SharedPrefsRepo
import com.droidfeed.databinding.ActivityOnboardBinding
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.util.CustomTab
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.extension.getClickableSpan
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class OnBoardActivity : BaseActivity() {

    @Inject lateinit var sharedPrefs: SharedPrefsRepo

    private val customTab = CustomTab(this)
    private val viewModel: OnBoardViewModel by viewModels { viewModelFactory }
    private lateinit var binding: ActivityOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setupFullScreenWindow()
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityOnboardBinding>(
            this,
            R.layout.activity_onboard
        ).apply {
            lifecycleOwner = this@OnBoardActivity
            cbAgreement.movementMethod = LinkMovementMethod.getInstance()

            onBoardViewModel = viewModel
            termsOfServiceSpan = getTermsOfUseSpan()
        }

        subscribeNavigationEvents()
    }

    private fun subscribeNavigationEvents() {
        viewModel.openUrl.observe(this, EventObserver { url ->
            customTab.showTab(url)
        })

        viewModel.showSnackBar.observe(this, EventObserver { stringId ->
            Snackbar.make(
                binding.root,
                stringId,
                Snackbar.LENGTH_LONG
            ).setAnchorView(binding.cbAgreement).show()
        })

        viewModel.openMainActivity.observe(this, EventObserver { continueToMainActivity() })
    }

    private fun getTermsOfUseSpan() = getString(
        R.string.i_agree_to,
        getString(R.string.terms_of_service)
    ).getClickableSpan(
        getString(R.string.terms_of_service)
    ) {
        viewModel.onTermsOfUseClicked()
    }

    private fun continueToMainActivity() {
        sharedPrefs.setHasAcceptedTerms(true)

        Intent(
            this,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }.run {
            startActivity(this)
        }
    }
}