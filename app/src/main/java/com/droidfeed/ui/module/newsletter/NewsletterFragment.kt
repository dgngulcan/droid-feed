package com.droidfeed.ui.module.newsletter

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.data.api.mailchimp.ErrorType
import com.droidfeed.databinding.FragmentNewsletterBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.DataState
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.extention.hideKeyboard
import com.droidfeed.util.extention.toggleVisibility

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

        viewModel.signUpEvent.observe(this, EventObserver { state ->
            when (state) {
                is DataState.Loading -> onLoading()
                is DataState.Success<*> -> onSignUpSuccess()
                is DataState.Error<*> -> onSignUpError(state)
            }
        })
    }

    private fun onLoading() {
        binding.apply {
            btnImIn.visibility = View.GONE
            txtAlreadySubscriber.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun onSignUpSuccess() {
        binding.apply {
            progressBar.visibility = View.GONE
            btnImIn.visibility = View.GONE
            textInputLayout.visibility = View.GONE
            txtSubscribed.visibility = View.VISIBLE
        }
    }

    private fun onSignUpError(state: DataState.Error<*>) {
        binding.apply {
            btnImIn.visibility = View.VISIBLE
            txtAlreadySubscriber.toggleVisibility(false)
            progressBar.visibility = View.GONE
        }

        if (state.data is ErrorType) {
            when (state.data) {
                ErrorType.MEMBER_ALREADY_EXIST -> {
                    binding.txtAlreadySubscriber.toggleVisibility(true)
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