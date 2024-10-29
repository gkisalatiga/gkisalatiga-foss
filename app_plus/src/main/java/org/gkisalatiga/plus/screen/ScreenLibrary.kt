/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the Bible.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.rajat.pdfviewer.util.saveTo
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType


class ScreenLibrary : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold (topBar = { getTopBar() }) {

            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) {
                // Display the main "attribution" contents.
                getMainContent()
            }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
        }

    }

    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current

        /* Displaying the markdown content. */
        Box(Modifier.background(Color.Transparent)) {
            val md: String = """
                # Sample Perpustakaan.
            """.trimIndent()

            // Display the markdown text.
            Column {
                val pdfRendererViewInstance = GlobalSchema.pdfRendererViewInstance!!

                val url = "https://myreport.altervista.org/Lorem_Ipsum.pdf"
                val headers = HeaderData()
                val lifecycleOwner = LocalLifecycleOwner.current
                val lifecycleScope = lifecycleOwner.lifecycleScope

                MarkdownText(
                    modifier = Modifier.padding(20.dp),
                    markdown = md.trimIndent(),
                    style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Justify)
                )
                Button (onClick = {
                    pdfRendererViewInstance.jumpToPage(3)
                }) {
                    Text("Jump to 3")
                }

                pdfRendererViewInstance.statusListener = object: PdfRendererView.StatusCallBack {
                    override fun onPageChanged(currentPage: Int, totalPage: Int) {
                        Logger.logRapidTest({}, "onPageChanged -> currentPage: $currentPage, totalPage: $totalPage", LoggerType.VERBOSE)
                        CurrentPage.mutablecurpg.intValue = currentPage
                    }

                    override fun onError(error: Throwable) {
                        super.onError(error)
                        CurrentPage.mutableString.value = error.message!!
                        Logger.logPDF({}, "onError -> error.message!!: ${error.message!!}")
                    }

                    override fun onPdfLoadProgress(
                        progress: Int,
                        downloadedBytes: Long,
                        totalBytes: Long?
                    ) {
                        super.onPdfLoadProgress(progress, downloadedBytes, totalBytes)
                        CurrentPage.mutableString.value = "Megunduh: $progress. $downloadedBytes/$totalBytes"
                        Logger.logPDF({}, "onPdfLoadProgress -> progress: $progress, downloadedBytes: $downloadedBytes, totalBytes: $totalBytes")
                    }

                    override fun onPdfLoadSuccess(absolutePath: String) {
                        super.onPdfLoadSuccess(absolutePath)
                        CurrentPage.mutableString.value = "Sukses mengunduh: $absolutePath"
                        Logger.logPDF({}, "onPdfLoadSuccess -> absolutePath: $absolutePath")
                    }

                    override fun onPdfLoadStart() {
                        super.onPdfLoadStart()
                        CurrentPage.mutableString.value = "Memuat file..>"
                        Logger.logPDF({}, "onPdfLoadStart (no parameter provided).")
                    }
                }

                // Text(CurrentPage.currentpg.toString())
                Text(CurrentPage.mutablecurpg.intValue.toString())
                Text(CurrentPage.mutableString.value)

                AndroidView(
                    factory = {
                        pdfRendererViewInstance.apply {
                            initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
                        }
                    },
                    update = {
                        // Update logic if needed
                    },
                    modifier = Modifier
                )

                // Placebo.
                Text("ew")
            }

        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(R.string.screenlibrary_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }
}

class CurrentPage : Application() {
    companion object {
        var currentpg = 0
        val mutablecurpg = mutableIntStateOf(0)
        val mutableString = mutableStateOf("")
    }
}