package com.droidfeed.ui.module.newsletter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.api.mailchimp.ErrorType
import com.droidfeed.databinding.FragmentNewsletterBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.util.extention.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_newsletter.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

@SuppressLint("ValidFragment")
class NewsletterFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsletterBinding
    private lateinit var viewModel: NewsletterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentNewsletterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(NewsletterViewModel::class.java)

        init()
    }

    private fun init() {
        binding.btnImIn.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

            when {
                email.isBlank() -> {
                    binding.textInputLayout.error = getString(R.string.error_empty_email)
                }
                !isValidEmail -> {
                    binding.textInputLayout.error = getString(R.string.error_email_format)
                }
                else -> {
                    it.hideKeyboard()
                    viewModel.signUp(email)
                    binding.textInputLayout.error = null
                }
            }
        }

        binding.textInputLayout.setErrorTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    activity!!,
                    R.color.redError
                )
            )
        )
        viewModel.signUpEvent.observe(this, Observer { resource ->
            when (resource?.dataState) {
                is DataStatus.Loading -> onLoading()
                is DataStatus.Success -> onSignUpSuccess()
                is DataStatus.Error<*> -> onSignUpError(resource.dataState as DataStatus.Error<*>)
            }
        })

        launch(UI) {
            delay(500)
            binding.animView.playAnimation()
        }
    }

    private fun onLoading() {
        binding.apply {
            btnImIn.visibility = View.GONE
            binding.textInputLayout.error = null
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun onSignUpSuccess() {
        binding.apply {
            progressBar.visibility = View.GONE
            btnImIn.visibility = View.GONE
            textInputLayout.visibility = View.GONE
            txtSubscriptionConfirmation.visibility = View.VISIBLE
        }
    }

    private fun onSignUpError(state: DataStatus.Error<*>) {
        binding.apply {
            btnImIn.visibility = View.VISIBLE
            binding.textInputLayout.error = null
            progressBar.visibility = View.GONE
        }

        if (state.data is ErrorType) {
            when (state.data) {
                ErrorType.MEMBER_ALREADY_EXIST -> {
                    binding.textInputLayout.error = getString(R.string.newsletter_email_exist)
                }

                ErrorType.INVALID_RESOURCE -> {
                    Snackbar.make(
                        binding.btnImIn,
                        getString(R.string.error_api_generic),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}