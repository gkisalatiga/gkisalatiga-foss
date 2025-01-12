/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom HTML body.
 * Only those HTML contents stored in the JSON schema's "data/static" node can be displayed.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.AgendaData
import org.gkisalatiga.plus.data.AgendaDataType
import org.gkisalatiga.plus.data.AgendaProposalStatus
import org.gkisalatiga.plus.data.CalendarData
import org.gkisalatiga.plus.data.TenseStatus
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.lib.AgendaCalculator
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONObject
import kotlin.math.ceil

class ScreenAgenda (private val current : ActivityData) : ComponentActivity() {

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
                ) {

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Display the dialog.
        getAgendaDetailDialog()

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {

        // The agenda node.
        val agendaJSONNode = MainCompanion.jsonRoot!!.getJSONObject("agenda")

        // Enlist the list of title, corresponding to name of days.
        val dayTitleList = agendaJSONNode.keys()

        // The column's saved scroll state.
        val scrollState = ScreenAgendaCompanion.rememberedScrollState!!
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = R.drawable.banner_agenda
            val imgDescription = "Menu banner"
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.aspectRatio(1.77778f)
            ) {
                Image(
                    painter = painterResource(imgSource),
                    contentDescription = imgDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            /* Enlisting the weekday label. */
            var isFirst = true
            Row (modifier = Modifier.fillMaxWidth()) {
                for (key in dayTitleList) {
                    val dayInLocale = StringFormatter.dayLocaleShortInIndonesian[key]!!
                    val startPadding = if (isFirst) 0.dp else 5.dp; isFirst = false
                    Text(
                        dayInLocale.uppercase(),
                        fontSize = 12.sp,
                        color = current.colors.screenAgendaWeekdayTextColor,
                        modifier = Modifier.weight(1.0f).padding(start = startPadding).wrapContentSize(Alignment.Center).padding(2.dp),
                        textAlign = TextAlign.Center
                    )  // --- end of Text.
                }
            }

            /* Enlisting the day selector chips. */

            val selected = ScreenAgendaCompanion.mutableCurrentDay

            val dateArray = AgendaCalculator.getDaysUpAhead(3)
            val columns = 7
            val rows = ceil((dateArray.size / columns).toDouble()).toInt()

            // Prevents NPE.
            if (selected.value == null) selected.value = dateArray.find { it.tense == TenseStatus.PRESENT }

            var index = 0
            for (j in 0 .. rows) {

                // Ensures that we don't draw an empty row when there is no data.
                if (index == dateArray.size) break

                @Suppress("NAME_SHADOWING") var isFirst = true
                Row {
                    while (index < dateArray.size) {

                        // The current date item that is being considered.
                        val item = dateArray[index]

                        val startPadding = if (isFirst) 0.dp else 5.dp; isFirst = false
                        Button(
                            onClick = { ScreenAgendaCompanion.mutableCurrentDay.value = item },
                            modifier = Modifier.padding(start = startPadding).weight(1.0f).width(intrinsicSize = IntrinsicSize.Max),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.dateString == selected.value!!.dateString) current.colors.screenAgendaChipSelectedBackgroundColor else {
                                    if (item.tense == TenseStatus.PRESENT) current.colors.screenAgendaChipTodayBackgroundColor else current.colors.screenAgendaChipUnselectedBackgroundColor
                                }
                            ),
                            // border = BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp),
                            enabled = item.tense != TenseStatus.PAST
                        ) {
                            Text(
                                item.dateString.split("-")[2].toInt().toString(),
                                fontSize = 18.sp,
                                color = if (item.dateString == selected.value!!.dateString) current.colors.screenAgendaChipTextSelectedBackgroundColor else current.colors.screenAgendaChipTextUnselectedBackgroundColor,
                                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).padding(2.dp),
                                textAlign = TextAlign.Center
                            )  // --- end of Text.
                        }

                        // Ensures that we have the right amount of columns.
                        index += 1
                        if (index % columns == 0) break
                    }

                    // Add spacer for non-even button rows. (Visual improvement.)
                    // Only applies to the last row.
                    if (j == rows) {
                        repeat(columns - (dateArray.size % columns)) {
                            Spacer(Modifier.weight(1f).padding(5.dp).aspectRatio(0.88888f))
                        }
                    }

                }
            }  // --- end of for loop.

            Spacer(Modifier.height(10.dp))

            // Filtering labels.
            val viewSelectionTextMap = mapOf(
                ScreenAgendaViewSelection.VIEW_ALL to stringResource(R.string.screen_agenda_type_chip_show_all_text),
                ScreenAgendaViewSelection.VIEW_ONLY_PROPOSAL_AGENDA to stringResource(R.string.screen_agenda_type_chip_show_only_proposal_text),
                ScreenAgendaViewSelection.VIEW_ONLY_REGULAR_AGENDA to stringResource(R.string.screen_agenda_type_chip_show_only_regular_text),
            )

            // Whether to display only regular agenda.
            Row (Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                ScreenAgendaViewSelection.entries.forEach {
                    val isSelected = ScreenAgendaCompanion.mutableCurrentViewSelection.value == it
                    fun changeViewSelection() { ScreenAgendaCompanion.mutableCurrentViewSelection.value = it }
                    /*Row (Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize.provides(Dp.Unspecified)) {
                            RadioButton(
                                modifier = Modifier.padding(5.dp),
                                selected = ScreenAgendaCompanion.mutableCurrentViewSelection.value == it,
                                onClick = { changeViewSelection() }
                            )
                        }
                        Spacer(Modifier.size(10.dp))
                        ClickableText(AnnotatedString(viewSelectionTextMap[it]!!), onClick = { changeViewSelection() }, modifier = Modifier.fillMaxWidth(), style = TextStyle(color = current.colors.screenSearchClickableTextColor))
                    }*/
                    FilterChip(
                        onClick = { changeViewSelection() },
                        label = { Text(viewSelectionTextMap[it]!!) },
                        selected = isSelected,
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors().copy(
                            containerColor = current.colors.screenAgendaChipUnselectedBackgroundAlternativeColor,
                            selectedContainerColor = current.colors.screenAgendaChipSelectedBackgroundColor,
                            leadingIconColor = current.colors.screenAgendaChipTextUnselectedBackgroundColor,
                            selectedLeadingIconColor = current.colors.screenAgendaChipTextSelectedBackgroundColor,
                            labelColor = current.colors.screenAgendaChipTextUnselectedBackgroundColor,
                            selectedLabelColor = current.colors.screenAgendaChipTextSelectedBackgroundColor,
                        ),
                        border = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(25.dp))

            // Get the day's name in current locale; then display the day title.
            val dayInLocale = StringFormatter.dayLocaleInIndonesian[selected.value!!.weekday]!!
            val dateInLocale = StringFormatter.convertDateFromJSON(selected.value!!.dateString)
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("$dayInLocale, $dateInLocale", modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(10.dp))

            /* Enumerate and enlist non-regular (proposal) schedules. */
            val proposalJSONArray = MainCompanion.jsonRoot!!.getJSONArray("agenda-ruangan")
            val enumeratedProposalList: MutableList<AgendaData> = mutableListOf()
            for (i in 0 until proposalJSONArray.length()) {
                val curNode = proposalJSONArray[i] as JSONObject
                enumeratedProposalList.add(
                    AgendaData(
                        name = curNode.getString("name"),
                        time = curNode.getString("time"),
                        timeTo = curNode.getString("time-to"),
                        timezone = curNode.getString("timezone"),
                        date = curNode.getString("date"),
                        weekday = curNode.getString("weekday"),
                        type = AgendaDataType.AGENDA_PROPOSAL,
                        place = curNode.getString("place"),
                        representative = curNode.getString("representative"),
                        pic = curNode.getString("pic"),
                        status = when (curNode.getString("status")) {
                            "c" -> AgendaProposalStatus.PROPOSAL_CANCELLED_BY_SUBMITTER
                            "n" -> AgendaProposalStatus.PROPOSAL_REJECTED
                            "y" -> AgendaProposalStatus.PROPOSAL_APPROVED
                            "w" -> AgendaProposalStatus.PROPOSAL_WAITING_FOR_APPROVAL
                            else -> AgendaProposalStatus.INTERNAL_ERROR
                        },
                        note = curNode.getString("note"),
                    )
                )
            }

            /* Enumerate and enlist regular schedules. */
            val regularJSONArray = agendaJSONNode.getJSONArray(selected.value!!.weekday)
            val enumeratedRegularList: MutableList<AgendaData> = mutableListOf()
            for (i in 0 until regularJSONArray.length()) {
                val curNode = regularJSONArray[i] as JSONObject
                enumeratedRegularList.add(
                    AgendaData(
                        name = curNode.getString("name"),
                        time = curNode.getString("time"),
                        timeTo = curNode.getString("time-to"),
                        timezone = curNode.getString("timezone"),
                        weekday = selected.value!!.weekday,
                        type = AgendaDataType.AGENDA_REGULAR,
                        place = curNode.getString("place"),
                        representative = curNode.getString("representative"),
                        status = AgendaProposalStatus.NOT_A_PROPOSAL,
                        note = curNode.getString("note"),
                    )
                )
            }

            /* Mixing and matching the schedules (both regular and proposal) of today. */
            val enumeratedFilteredList: List<AgendaData> = mutableListOf<AgendaData>()
                .let {
                    ScreenAgendaCompanion.mutableCurrentViewSelection.value.let { curView ->
                        if (curView in listOf(ScreenAgendaViewSelection.VIEW_ALL, ScreenAgendaViewSelection.VIEW_ONLY_REGULAR_AGENDA))
                            enumeratedRegularList.forEach { item -> it.add(item) }
                        if (curView in listOf(ScreenAgendaViewSelection.VIEW_ALL, ScreenAgendaViewSelection.VIEW_ONLY_PROPOSAL_AGENDA))
                            enumeratedProposalList.forEach { item ->
                                if (item.date == selected.value!!.dateString) it.add(item)
                            }
                        it.sortedBy { criteria -> criteria.time }
                    }
                }

            enumeratedFilteredList.forEach {
                // The status strings, localized.
                val localizedStatusStringList = mapOf(
                    AgendaProposalStatus.PROPOSAL_CANCELLED_BY_SUBMITTER to stringResource(R.string.screen_agenda_status_cancelled),
                    AgendaProposalStatus.PROPOSAL_APPROVED to stringResource(R.string.screen_agenda_status_approved),
                    AgendaProposalStatus.INTERNAL_ERROR to stringResource(R.string.screen_agenda_status_internal_error),
                    AgendaProposalStatus.NOT_A_PROPOSAL to stringResource(R.string.screen_agenda_status_not_a_proposal),
                    AgendaProposalStatus.PROPOSAL_REJECTED to stringResource(R.string.screen_agenda_status_denied),
                    AgendaProposalStatus.PROPOSAL_WAITING_FOR_APPROVAL to stringResource(R.string.screen_agenda_status_waiting),
                )

                // Determine the status.
                val status = it.status
                var statusLabelColor: Color? = null
                var statusIcon: ImageVector? = null
                when (status) {
                    AgendaProposalStatus.PROPOSAL_APPROVED -> {
                        statusLabelColor = current.colors.screenAgendaStatusApprovedColor
                        statusIcon = Icons.Default.CheckCircle
                    }
                    AgendaProposalStatus.INTERNAL_ERROR -> {
                        statusLabelColor = current.colors.screenAgendaContentTintColor
                        statusIcon = Icons.AutoMirrored.Filled.Help
                    }
                    AgendaProposalStatus.NOT_A_PROPOSAL -> {
                        statusLabelColor = current.colors.screenAgendaContentTintColor
                        statusIcon = Icons.AutoMirrored.Filled.Help
                    }
                    AgendaProposalStatus.PROPOSAL_REJECTED -> {
                        statusLabelColor = current.colors.screenAgendaStatusDeniedColor
                        statusIcon = Icons.Default.DoNotDisturbOn
                    }
                    AgendaProposalStatus.PROPOSAL_CANCELLED_BY_SUBMITTER -> {
                        statusLabelColor = current.colors.screenAgendaStatusCancelledColor
                        statusIcon = Icons.Default.Cancel
                    }
                    AgendaProposalStatus.PROPOSAL_WAITING_FOR_APPROVAL -> {
                        statusLabelColor = current.colors.screenAgendaStatusWaitingColor
                        statusIcon = Icons.Default.Pending
                    }
                }

                // Determine what to do upon surface click.
                fun onSurfaceClick() {
                    ScreenAgendaCompanion.mutableShowAgendaInfoDialog.value = true
                    ScreenAgendaCompanion.currentInfoDialogData = it
                    ScreenAgendaCompanion.currentInfoDialogProposalStatusIcon = statusIcon
                    ScreenAgendaCompanion.currentInfoDialogProposalStatusColor = statusLabelColor
                    ScreenAgendaCompanion.currentInfoDialogProposalStatusLocalizedString = localizedStatusStringList[status]!!
                }

                // Draw the list item for the current event.
                Surface(shape = RoundedCornerShape(15.dp), modifier = Modifier.padding(top = 10.dp), onClick = { onSurfaceClick() }) {
                    Column (Modifier.fillMaxSize().background(current.colors.screenAgendaItemBackgroundColor)) {
                        Column (modifier = Modifier.fillMaxSize().padding(15.dp)) {
                            // The item title.
                            Text(it.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(5.dp))

                            // Determining the top padding of each auxiliary information.
                            val infoTopPadding = 4.dp

                            // Displaying the place info.
                            Row (verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = infoTopPadding)) {
                                Icon(Icons.Default.Place, "", tint = current.colors.screenAgendaContentTintColor)
                                Spacer(Modifier.width(10.dp))

                                val text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(stringResource(R.string.screen_agenda_info_label_place))
                                    }
                                    append(": ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)) {
                                        append(it.place)
                                    }
                                }
                                Text(text, fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor)
                            }

                            // Displaying the representative info.
                            Row (verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = infoTopPadding)) {
                                Icon(Icons.Default.Groups, "", tint = current.colors.screenAgendaContentTintColor)
                                Spacer(Modifier.width(10.dp))

                                val text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(stringResource(R.string.screen_agenda_info_label_representative))
                                    }
                                    append(": ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)) {
                                        append(it.representative)
                                    }
                                }
                                Text(text, fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor)
                            }

                            // Display the PIC.
                            val pic = it.pic
                            if (pic.isNotEmpty() && pic.strip() != "-")
                                Row (verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = infoTopPadding)) {
                                    Icon(Icons.Default.Contacts, "", tint = current.colors.screenAgendaContentTintColor)
                                    Spacer(Modifier.width(10.dp))

                                    val text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(stringResource(R.string.screen_agenda_info_label_pic))
                                        }
                                        append(": ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)) {
                                            append(pic)
                                        }
                                    }
                                    Text(text, fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor)
                                }

                            // Display additional note.
                            val note = it.note
                            if (note.isNotEmpty() && note.strip() != "-")
                                Row (verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = infoTopPadding)) {
                                    Icon(Icons.AutoMirrored.Default.Article, "", tint = current.colors.screenAgendaContentTintColor)
                                    Spacer(Modifier.width(10.dp))

                                    val text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(stringResource(R.string.screen_agenda_info_label_notes))
                                        }
                                        append(": ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)) {
                                            append(note)
                                        }
                                    }
                                    Text(text, fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor)
                                }

                            // Display the status info.
                            if (status != AgendaProposalStatus.NOT_A_PROPOSAL)
                                Row (verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = infoTopPadding)) {
                                    Icon(statusIcon!!, "", tint = statusLabelColor!!)
                                    Spacer(Modifier.width(10.dp))

                                    val text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(stringResource(R.string.screen_agenda_info_label_status))
                                        }
                                        append(": ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)) {
                                            append(localizedStatusStringList[status]!!)
                                        }
                                    }
                                    Text(text, fontSize = 14.sp, color = statusLabelColor!!)
                                }
                        }

                        // Display time info.
                        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxSize()) {
                            Surface(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp)) {
                                Column(modifier = Modifier.background(current.colors.screenAgendaItemProposalRutinBackgroundColor)) {
                                    Row {
                                        val typeText = if (it.type == AgendaDataType.AGENDA_REGULAR) stringResource(R.string.screen_agenda_type_regular_text) else stringResource(R.string.screen_agenda_type_proposal_text)
                                        Text(typeText.uppercase(), modifier = Modifier.padding(horizontal = 10.dp).padding(vertical = 5.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = current.colors.screenAgendaItemProposalStatusTextColor)
                                        Surface(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp)) {
                                            Column(modifier = Modifier.background(current.colors.screenAgendaItemTimeBackgroundColor)) {
                                                val timeText = it.time + " - " + it.timeTo
                                                Text( timeText, modifier = Modifier.padding(horizontal = 10.dp).padding(vertical = 5.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = current.colors.screenAgendaItemTimeTextColor)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }  // --- end of surface.
            }  // --- end of forEach.
        }  // --- end of column.

    }
    
    @Composable
    private fun getAgendaDetailDialog() {
        // Show the PDF information dialog.
        if (ScreenAgendaCompanion.mutableShowAgendaInfoDialog.value) {
            if (ScreenAgendaCompanion.currentInfoDialogData != null) {
                val infoDialogTitle = stringResource(R.string.screen_agenda_info_dialog_title)
                val infoDialogContentLabelName = stringResource(R.string.screen_agenda_info_dialog_label_name)
                val infoDialogContentLabelPlace = stringResource(R.string.screen_agenda_info_dialog_label_place)
                val infoDialogContentLabelRepresentative = stringResource(R.string.screen_agenda_info_dialog_label_representative)
                val infoDialogContentLabelPic = stringResource(R.string.screen_agenda_info_dialog_label_pic)
                val infoDialogContentLabelNotes = stringResource(R.string.screen_agenda_info_dialog_label_notes)
                val infoDialogContentLabelStatus = stringResource(R.string.screen_agenda_info_dialog_label_status)

                // Assert non-null.
                val data = ScreenAgendaCompanion.currentInfoDialogData!!

                // Rendering the composable element.
                AlertDialog(
                    onDismissRequest = {
                        ScreenAgendaCompanion.mutableShowAgendaInfoDialog.value = false
                    },
                    title = { Text(infoDialogTitle) },
                    text = {
                        Column (Modifier.fillMaxWidth().height(325.dp).verticalScroll(rememberScrollState())) {
                            Text(infoDialogContentLabelName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(data.name)
                            Spacer(Modifier.fillMaxWidth().height(10.dp))

                            Text(infoDialogContentLabelPlace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(data.place)
                            Spacer(Modifier.fillMaxWidth().height(10.dp))

                            Text(infoDialogContentLabelRepresentative, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(data.representative)
                            Spacer(Modifier.fillMaxWidth().height(10.dp))

                            if (data.pic.isNotBlank() && data.pic.strip() != "-") {
                                Text(infoDialogContentLabelPic, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(data.pic)
                                Spacer(Modifier.fillMaxWidth().height(10.dp))
                            }

                            if (data.note.isNotBlank() && data.note.strip() != "-") {
                                Text(infoDialogContentLabelNotes, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(data.note)
                                Spacer(Modifier.fillMaxWidth().height(10.dp))
                            }

                            Text(infoDialogContentLabelStatus, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row (verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(ScreenAgendaCompanion.currentInfoDialogProposalStatusIcon!!, "", tint = ScreenAgendaCompanion.currentInfoDialogProposalStatusColor)
                                Spacer(Modifier.width(10.dp))
                                Text(ScreenAgendaCompanion.currentInfoDialogProposalStatusLocalizedString, fontSize = 14.sp, color = ScreenAgendaCompanion.currentInfoDialogProposalStatusColor)
                            }
                            Spacer(Modifier.fillMaxWidth().height(10.dp))
                        }
                    },
                    confirmButton = {
                        Button(onClick = { ScreenAgendaCompanion.mutableShowAgendaInfoDialog.value = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screenagenda_title),
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

}

class ScreenAgendaCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* Whether to display regular agenda. */
        val mutableCurrentViewSelection = mutableStateOf(ScreenAgendaViewSelection.VIEW_ALL)

        /* The currently selected day chip. */
        var mutableCurrentDay = mutableStateOf<CalendarData?>(null)

        /* Display the info dialog. */
        val mutableShowAgendaInfoDialog = mutableStateOf(false)
        var currentInfoDialogData: AgendaData? = null
        var currentInfoDialogProposalStatusIcon: ImageVector? = null
        var currentInfoDialogProposalStatusColor: Color = Color.Unspecified
        var currentInfoDialogProposalStatusLocalizedString = String()
    }
}

enum class ScreenAgendaViewSelection {
    VIEW_ALL,
    VIEW_ONLY_REGULAR_AGENDA,
    VIEW_ONLY_PROPOSAL_AGENDA,
}
