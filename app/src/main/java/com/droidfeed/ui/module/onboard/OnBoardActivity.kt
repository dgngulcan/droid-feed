package com.droidfeed.ui.module.onboard

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.droidfeed.R
import com.droidfeed.databinding.ActivityOnboardBinding
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.util.isMarshmallow

class OnBoardActivity : BaseActivity() {

    lateinit var binding: ActivityOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
            setupFullScreenWindow()

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboard)


    }

}