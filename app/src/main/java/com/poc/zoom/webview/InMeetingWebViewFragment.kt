package com.poc.zoom.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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

        binding.layoutInMeetingWebviewClose.setOnClickListener {
            dismiss()
        }
        setupWeb()
    }

    private fun setupWeb() {
        binding.layoutInMeetingWeb.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        binding.layoutInMeetingWebviewProgress.visibility = View.GONE
                    }
                }
            }
            loadUrl(args.url)
        }
    }

}