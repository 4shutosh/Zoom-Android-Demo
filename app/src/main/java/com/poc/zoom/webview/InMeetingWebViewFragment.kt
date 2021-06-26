package com.poc.zoom.webview

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.poc.zoom.R
import com.poc.zoom.databinding.LayoutInMeetingWebviewBinding


class InMeetingWebViewFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutInMeetingWebviewBinding

    private val args: InMeetingWebViewFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.InMeetingBottomWebViewClientStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutInMeetingWebviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {

        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        view?.post {
            val parent: View = view?.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            view?.measuredHeight?.let { bottomSheetBehavior?.setPeekHeight(it) }
        }

        binding.layoutInMeetingWebviewUrl.setText(args.url)

        binding.layoutInMeetingWebviewSearch.setOnClickListener {
            val url = binding.layoutInMeetingWebviewUrl.text.toString()
            if (url.isNotEmpty()) {
                binding.layoutInMeetingWebviewUrl.clearFocus()
                binding.layoutInMeetingWeb.loadUrl(url)
                view?.let {
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                }
            }
        }

        binding.layoutInMeetingWebviewClose.setOnClickListener {
            dismiss()
        }
        setupWeb()
    }

    private fun setupWeb() {
        binding.layoutInMeetingWeb.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.allowContentAccess = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        binding.layoutInMeetingWebviewProgress.visibility = View.GONE
                    } else {
                        binding.layoutInMeetingWebviewProgress.visibility = View.VISIBLE
                    }
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    binding.layoutInMeetingWebviewUrl.setText(url)
                    return super.shouldOverrideUrlLoading(view, url)
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    binding.layoutInMeetingWebviewUrl.setText(request?.url.toString())
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
            loadUrl(args.url)
        }
    }

}