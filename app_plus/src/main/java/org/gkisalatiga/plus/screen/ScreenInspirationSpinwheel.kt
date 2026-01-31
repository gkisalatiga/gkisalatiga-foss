/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NoiseAware
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.commandiron.spin_wheel_compose.SpinWheel
import com.commandiron.spin_wheel_compose.SpinWheelDefaults
import com.commandiron.spin_wheel_compose.state.rememberSpinWheelState
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.ModulesInspirationContentItemObject
import org.gkisalatiga.plus.data.ModulesInspirationObject
import org.gkisalatiga.plus.fragment.FragmentInfoCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import kotlin.math.round

private val emptyModulesInspirationObject = ModulesInspirationObject(
    title = "",
    desc = "",
    type = "",
    banner = "",
    uuid = "",
    tags = mutableListOf(),
    date = 0,
    isShown = 0,
    content = mutableListOf(),
)

class ScreenInspirationSpinwheel (private val current : ActivityData) : ComponentActivity() {

    companion object {
        public val defaultIconPools = mutableListOf(
            Icons.Default.Star,
            Icons.Default.Pentagon,
            Icons.Default.Square,
            Icons.Default.Hexagon,
            Icons.Default.Circle,
            Icons.Default.Lightbulb,
            Icons.Default.NearMe,
        )
    }

    /* Closed list of the pool of icons. */
    private val iconPools = listOf(
        Icons.Default.Star,
        Icons.Default.Pentagon,
        Icons.Default.Square,
        Icons.Default.Hexagon,
        Icons.Default.Circle,
        Icons.Default.Lightbulb,
        Icons.Default.NearMe,
        Icons.Default.People,
        Icons.Default.Save,
        Icons.Default.School,
        Icons.Default.Landscape,
        Icons.Default.Brightness7,
        Icons.Default.PanTool,
        Icons.Default.Place,
        Icons.Default.Hardware,
        Icons.Default.Category,
        Icons.Default.Timer,
        Icons.Default.NoiseAware,
        Icons.Default.QuestionAnswer,
        Icons.Default.AddReaction,
        Icons.Default.Cancel,
        Icons.Default.InvertColors,
    )

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        /* Shuffle the list of random items. */
        LaunchedEffect(Unit) {
            handleShuffleData()
        }

        Scaffold (topBar = { getTopBar() }) {
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) { getMainContent() }
        }

        getSpinWheelItemDetailDialog()
        getSpinWheelListDialog()

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {
        val scrollState = ScreenInspirationSpinwheelCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            /** TODO: Remove.
            val wheelData: MutableList<WheelData> = mutableListOf()

            ScreenInspirationSpinwheelCompanion.inspirationData.content.forEach {
            Text(it.string)
            val item = WheelData(
            text = it.id.toString(),
            textColor = listOf(Color.Black),
            backgroundColor = listOf(Color.White),
            )
            wheelData.add(item)
            }

            val context = current.ctx

            var startRotate = mutableStateOf(false)
            val wheelViewState = mutableStateOf(WheelViewState(startRotate.value))

            Column(
            modifier = Modifier
            .fillMaxSize()
            .background(Color(170, 170, 170)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            LuckyWheelView(
            modifier = Modifier
            .width(350.dp)
            .height(350.dp)
            .padding(vertical = 30.dp),
            wheelItems = wheelData,
            target = 3,
            onRotationComplete = { wheelData ->
            // do something with winner wheel data
            Toast.makeText(context, wheelData.text, Toast.LENGTH_LONG).show()
            },
            onRotationStatus = { status ->
            when (status) {
            RotationStatus.ROTATING -> { // do something
            }

            RotationStatus.IDLE -> { // do something
            }

            RotationStatus.COMPLETED -> { // do something
            }

            RotationStatus.CANCELED -> { // do something
            }
            }
            },
            wheelViewState = wheelViewState.value
            )

            Button(modifier = Modifier.padding(top = 20.dp), onClick = {
            startRotate.value = true
            }) {
            Text("Spin!")
            }
            }
             */

            // TESTING.
            val spinWheelState = rememberSpinWheelState(
                pieCount = ScreenInspirationSpinwheelCompanion.pieSlices,
                durationMillis = 10000,
                delayMillis = 200,
                rotationPerSecond = 2f,
                easing = LinearOutSlowInEasing,
                startDegree = 0f,
            )
            /* Spacer between the top bar and the spin wheel. */
            Spacer(modifier = Modifier.height(20.dp))

            SpinWheel(
                state = spinWheelState,
                dimensions = SpinWheelDefaults.spinWheelDimensions(
                    spinWheelSize = 320.dp,
                    frameWidth = 20.dp,
                    selectorWidth = 10.dp
                ),
                colors = SpinWheelDefaults.spinWheelColors(
                    frameColor = Color(0xFF403d39),
                    dividerColor = Color(0xFFfffcf2),
                    selectorColor = Color(0xFFdc0073),
                    pieColors = listOf(
                        Color(0xFFdabfff),
                        Color(0xFF907ad6),
                        Color(0xFF4f518c),
                        Color(0xFF2c2a4a)
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ScreenInspirationSpinwheelCompanion.mutableShuffledIconData!!.value[it],
                    tint = Color.White,
                    contentDescription = ""
                )
            }

            /* Spacer between the spinwheel and the buttons. */
            Spacer(modifier = Modifier.height(32.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { handleShuffleData() }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        tint = Color.White,
                        contentDescription = "Shuffle the pie"
                    )
                }
                Button(
                    onClick = {
                        current.scope.launch {
                            spinWheelState.spin(
                                onFinish = {
                                    ScreenInspirationSpinwheelCompanion.mutableShowListDialog.value = false  // --- in case it is still opened.
                                    ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = false  // --- in case it is still opened.
                                    ScreenInspirationSpinwheelCompanion.mutableSelectedItemDetailData = ScreenInspirationSpinwheelCompanion.mutableShuffledSpinWheelData!!.value[it]
                                    ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = true
                                    Toast.makeText(current.ctx, "[EXPORT] 40 Hari Niat baik telah terpilih!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.padding(horizontal = 10.dp).width(200.dp)
                ) {
                    Text(stringResource(R.string.screen_inspiration_spinwheel_spinwheel_button_label))
                }
                IconButton(
                    onClick = {
                        ScreenInspirationSpinwheelCompanion.mutableShowListDialog.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                        tint = Color.White,
                        contentDescription = "List all items"
                    )
                }
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            /* Displaying the individual shuffled items. */
            ScreenInspirationSpinwheelCompanion.mutableShuffledSpinWheelData!!.value.forEachIndexed { idx, it ->
                Card(
                    onClick = {
                        ScreenInspirationSpinwheelCompanion.mutableSelectedItemDetailData = it
                        ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = true
                    },
                    modifier = Modifier
                ) {
                    Column ( modifier = Modifier.fillMaxWidth().padding(5.dp), verticalArrangement = Arrangement.Center ) {
                        Row {
                            Icon(
                                imageVector = ScreenInspirationSpinwheelCompanion.mutableShuffledIconData!!.value[idx],
                                tint = Color.White,
                                contentDescription = "List all items"
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(it.string, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }

    /**
     * For detailed.
    Card(
    onClick = {},
    modifier = Modifier.padding(bottom = 5.dp)
    ) {
    Column ( modifier = Modifier.fillMaxWidth().padding(5.dp), verticalArrangement = Arrangement.Center ) {
    Row {
    // The first post thumbnail.
    Surface (shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1.0f).fillMaxHeight().padding(start = 5.dp)) {
    AsyncImage(
    it.pictureUrl,
    contentDescription = "",
    error = painterResource(R.drawable.thumbnail_error),
    placeholder = painterResource(R.drawable.thumbnail_placeholder),
    modifier = Modifier.aspectRatio(1f).width(7.5.dp),
    contentScale = ContentScale.Crop
    )
    }
    Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
    Text(it.string, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
    }
    }
    }
     */

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("ComposableNaming")
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    ScreenInspirationSpinwheelCompanion.inspirationData.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { AppNavigation.popBack() }) {
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

    /**
     * Handles the notification of each individual spinwheel item.
     */
    @Composable
    private fun getSpinWheelItemDetailDialog() {
        // The button strings.
        val cancelBtnLabel = stringResource(R.string.fragment_info_dialog_cancel_btn)
        val okBtnLabel = stringResource(R.string.fragment_info_dialog_ok_btn)

        if (ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value) {
            // The data.
            val data = ScreenInspirationSpinwheelCompanion.mutableSelectedItemDetailData!!

            // The grand title.
            val grandTitle = ScreenInspirationSpinwheelCompanion.inspirationData.title

            // Rendering the composable element.
            AlertDialog(
                onDismissRequest = {
                    ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = false
                },
                title = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Surface (shape = RoundedCornerShape(10.dp), modifier = Modifier.width(200.dp).padding(bottom = 10.dp)) {
                            AsyncImage(
                                data.pictureUrl,
                                "",
                                modifier = Modifier.aspectRatio(1.0f).fillMaxWidth(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Text("$grandTitle #${data.id + 1}", color = current.colors.fragmentInfoIconTintColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column (
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(data.string, color = current.colors.fragmentInfoIconTintColor, textAlign = TextAlign.Center, overflow = TextOverflow.Visible)
                    }
                },
                confirmButton = {
                    Row {
                        TextButton(onClick = { ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = false }) {
                            Text(cancelBtnLabel)
                        }
                        Spacer(Modifier.width(10.dp))
                        Button(onClick = { ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = false }) {
                            Text(okBtnLabel, color = Color.White)
                        }
                    }
                }
            )
        }
    }

    /**
     * Handles the notification of list of all items.
     */
    @Composable
    private fun getSpinWheelListDialog() {
        // The button strings.
        val okBtnLabel = stringResource(R.string.fragment_info_dialog_cancel_btn)

        if (ScreenInspirationSpinwheelCompanion.mutableShowListDialog.value) {
            // The grand title.
            val grandTitle = ScreenInspirationSpinwheelCompanion.inspirationData.title

            // Rendering the composable element.
            AlertDialog(
                onDismissRequest = {
                    ScreenInspirationSpinwheelCompanion.mutableShowListDialog.value = false
                },
                title = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(grandTitle, color = current.colors.fragmentInfoIconTintColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column (
                        Modifier.fillMaxWidth().heightIn(100.dp, 300.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScreenInspirationSpinwheelCompanion.inspirationData.content.forEach {
                            Card(
                                onClick = {
                                    ScreenInspirationSpinwheelCompanion.mutableShowListDialog.value = false
                                    ScreenInspirationSpinwheelCompanion.mutableSelectedItemDetailData = it
                                    ScreenInspirationSpinwheelCompanion.mutableShowItemDetailDialog.value = true
                                },
                                modifier = Modifier.padding(bottom = 5.dp)
                            ) {
                                Column ( modifier = Modifier.fillMaxWidth().padding(5.dp), verticalArrangement = Arrangement.Center ) {
                                    Row {
                                        // The first post thumbnail.
                                        Surface (shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1.0f).fillMaxHeight().padding(start = 5.dp)) {
                                            AsyncImage(
                                                it.pictureUrl,
                                                contentDescription = "",
                                                error = painterResource(R.drawable.thumbnail_error),
                                                placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                                modifier = Modifier.aspectRatio(1f).width(7.5.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
                                            Text(it.string, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { ScreenInspirationSpinwheelCompanion.mutableShowListDialog.value = false }) {
                        Text(okBtnLabel, color = Color.White)
                    }
                }
            )
        }
    }

    /**
     * This function handles the shuffling of the spinwheel data.
     */
    private fun handleShuffleData() {
        val allPies = ScreenInspirationSpinwheelCompanion.inspirationData.content
        val slices = ScreenInspirationSpinwheelCompanion.pieSlices
        val allowDuplicate = allPies.size < slices

        // Hard reset.
        ScreenInspirationSpinwheelCompanion.mutableShuffledSpinWheelData!!.value = mutableListOf()

        // Temporary store of data.
        val reservedIndices: MutableList<Int> = mutableListOf()

        // Ensuring all data gets displayed when allPies.size < slices.
        if (allPies.size < slices) {
            repeat(allPies.size) {
                while (true) {
                    val newRandIdx = round(Math.random() * (allPies.size - 1)).toInt()
                    if (newRandIdx in reservedIndices) {
                        continue
                    }
                    reservedIndices.add(newRandIdx)
                    break
                }
            }
        }

        repeat (slices - reservedIndices.size) {
            while (true) {
                val newRandIdx = round(Math.random() * (allPies.size - 1)).toInt()
                if (!allowDuplicate && newRandIdx in reservedIndices) {
                    continue
                }
                reservedIndices.add(newRandIdx)
                break
            }
        }

        reservedIndices.forEach {
            ScreenInspirationSpinwheelCompanion.mutableShuffledSpinWheelData!!.value.add(allPies[it])
        }

        // Finally, handle the icons.
        val newIconList = mutableListOf<ImageVector>()
        repeat (slices) {
            while (true) {
                val newRandIdx = round(Math.random() * (iconPools.size - 1)).toInt()
                if (iconPools[newRandIdx] in newIconList) continue
                newIconList.add(iconPools[newRandIdx])
                break
            }
        }
        ScreenInspirationSpinwheelCompanion.mutableShuffledIconData!!.value = newIconList
    }
}

class ScreenInspirationSpinwheelCompanion : Application() {
    companion object {
        /* The spinwheel data. */
        var inspirationData: ModulesInspirationObject = emptyModulesInspirationObject

        /* The shuffled spinwheel data. */
        var mutableShuffledIconData: MutableState<MutableList<ImageVector>>? = null
        var mutableShuffledSpinWheelData: MutableState<MutableList<ModulesInspirationContentItemObject>>? = null

        /* States for dialogues. */
        var mutableShowListDialog = mutableStateOf(false)
        var mutableShowItemDetailDialog = mutableStateOf(false)
        var mutableSelectedItemDetailIndex = mutableStateOf(0)
        var mutableSelectedItemDetailData: ModulesInspirationContentItemObject? = null

        /* The number of slices per spin wheel. */
        val pieSlices = 7

        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}