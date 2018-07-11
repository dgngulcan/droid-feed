package com.droidfeed.ui.module.newsletter

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.data.api.mailchimp.MailchimpErrorType
import com.droidfeed.databinding.FragmentNewsletterBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.DataState
import com.droidfeed.ui.common.EventObserver
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 4/15/18.
 */
@SuppressLint("ValidFragment")
class NewsletterFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsletterBinding
    private lateinit var viewModel: NewsletterViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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
            .of(
                this,
                viewModelFactory
            )
            .get(NewsletterViewModel::class.java)

        init()
    }

    private fun init() {
        binding.btnImIn.setOnClickListener { onSignUp(binding.edtEmail.text) }

        viewModel.signUpEvent.observe(this, EventObserver { state ->
            when (state) {
                is DataState.Success<*> -> onSignUpSuccess()
                is DataState.Error<*> -> onSignUpError(state)
            }
        })
    }

    private fun onSignUpError(state: DataState.Error<*>) {
        if (state.data is MailchimpErrorType) {
            when (state.data) {
                MailchimpErrorType.MEMBER_ALREADY_EXIST -> {
                }
            }
        }

    }

    private fun onSignUpSuccess() {


    }

    /**
     * Handles sign up event.
     */
    private fun onSignUp(email: Editable) {
        when {
            email.isBlank() -> binding.textInputLayout.error = getString(R.string.error_empty_email)
            Patterns.EMAIL_ADDRESS.matcher(email).matches() -> viewModel.signUp(email.toString())
            else -> binding.textInputLayout.error = getString(R.string.error_email_format)
        }
    }

}