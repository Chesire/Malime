package com.chesire.malime.view.login.kitsu

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.chesire.malime.R
import com.chesire.malime.databinding.FragmentKitsuLoginBinding
import com.chesire.malime.injection.Injectable
import com.chesire.malime.util.autoCleared
import com.chesire.malime.util.extension.hideSystemKeyboard
import com.chesire.malime.view.login.BaseLoginFragment
import javax.inject.Inject

private const val KITSU_SIGNUP_URL = "https://kitsu.io/explore/anime"

class KitsuLoginFragment : BaseLoginFragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: KitsuLoginViewModel
    private var binding by autoCleared<FragmentKitsuLoginBinding>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(KitsuLoginViewModel::class.java)
            .apply {
                loginResponse.observe(
                    this@KitsuLoginFragment,
                    Observer {
                        processLoginResponse(it)
                    }
                )
                errorResponse.observe(
                    this@KitsuLoginFragment,
                    Observer {
                        processErrorResponse(it)
                    }
                )
            }
        binding.vm = viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil
            .inflate<FragmentKitsuLoginBinding>(
                inflater,
                R.layout.fragment_kitsu_login,
                container,
                false
            ).apply {
                binding = this
                loginButton.setOnClickListener { executeLoginMethod() }
                loginCreateAccount.setOnClickListener { createAccount() }
                loginPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        executeLoginMethod()
                    }
                    false
                }
            }.root
    }

    private fun createAccount() {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, Uri.parse(KITSU_SIGNUP_URL))
    }

    private fun executeLoginMethod() {
        requireActivity().hideSystemKeyboard(requireContext())
        viewModel.executeLogin()
    }

    companion object {
        const val tag = "KitsuLoginFragment"
        fun newInstance(): KitsuLoginFragment {
            val kitsuLoginFragment = KitsuLoginFragment()
            val args = Bundle()
            kitsuLoginFragment.arguments = args
            return kitsuLoginFragment
        }
    }
}