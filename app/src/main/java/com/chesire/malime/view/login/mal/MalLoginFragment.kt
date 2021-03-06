package com.chesire.malime.view.login.mal

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chesire.malime.R
import com.chesire.malime.databinding.FragmentMalLoginBinding
import com.chesire.malime.util.autoCleared
import com.chesire.malime.util.extension.hideSystemKeyboard
import com.chesire.malime.view.login.BaseLoginFragment
import javax.inject.Inject

private const val MAL_SIGNUP_URL = "https://myanimelist.net/register.php"

class MalLoginFragment : BaseLoginFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MalLoginViewModel
    private var binding by autoCleared<FragmentMalLoginBinding>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(MalLoginViewModel::class.java)
            .apply {
                loginResponse.observe(
                    viewLifecycleOwner,
                    Observer {
                        processLoginResponse(it)
                    }
                )
                errorResponse.observe(
                    viewLifecycleOwner,
                    Observer {
                        processErrorResponse(it)
                    }
                )
            }
        binding.vm = viewModel
        binding.setLifecycleOwner(viewLifecycleOwner)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil
            .inflate<FragmentMalLoginBinding>(
                inflater,
                R.layout.fragment_mal_login,
                container,
                false
            ).apply {
                binding = this
                fragmentMalLoginButton.setOnClickListener { executeLoginMethod() }
                fragmentMalLoginCreateAccountText.setOnClickListener { createAccount() }
                fragmentMalLoginPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
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
            .launchUrl(context, Uri.parse(MAL_SIGNUP_URL))
    }

    private fun executeLoginMethod() {
        requireActivity().hideSystemKeyboard(requireContext())

        // We have to convert to base64 here, or the unit tests won't work as Base64 is an Android class
        viewModel.executeLogin(
            Base64.encodeToString(
                "${viewModel.loginModel.userName}:${viewModel.loginModel.password}".toByteArray(
                    Charsets.UTF_8
                ), Base64.NO_WRAP
            )
        )
    }

    companion object {
        const val tag = "MalLoginFragment"
        fun newInstance(): MalLoginFragment {
            val loginFragment = MalLoginFragment()
            val args = Bundle()
            loginFragment.arguments = args
            return loginFragment
        }
    }
}
