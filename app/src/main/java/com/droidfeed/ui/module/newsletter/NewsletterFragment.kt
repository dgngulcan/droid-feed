package com.droidfeed.ui.module.newsletter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.droidfeed.databinding.FragmentNewsletterBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.util.event.EventObserver
import com.google.android.material.snackbar.Snackbar

@SuppressLint("ValidFragment")
class NewsletterFragment : BaseFragment("newsletter") {

    private lateinit var binding: FragmentNewsletterBinding
    private lateinit var newsletterViewModel: NewsletterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newsletterViewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(NewsletterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentNewsletterBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            viewModel = newsletterViewModel
            setLifecycleOwner(this@NewsletterFragment)
        }

        subscribeErrorSnack()

        return binding.root
    }

    private fun subscribeErrorSnack() {
        newsletterViewModel.showErrorSnack.observe(viewLifecycleOwner, EventObserver { stringId ->
            Snackbar.make(
                binding.root,
                stringId,
                Snackbar.LENGTH_SHORT
            ).show()
        })

    }
}
