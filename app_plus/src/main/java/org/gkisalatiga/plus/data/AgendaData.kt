/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Calculates, sorts, and filters both regular agenda and agenda proposal.
 */

package org.gkisalatiga.plus.data

data class AgendaData (
    val name: String,
    val time: String,
    val timeTo: String,
    val place: String,
    val representative: String,
    val note: String,
    val timezone: String,
    val weekday: String,
    val type: AgendaDataType,

    // Only for non-regular agenda items.
    val date: String = String(),
    val pic: String = String(),
    val status: AgendaProposalStatus = AgendaProposalStatus.NOT_A_PROPOSAL,
)

enum class AgendaDataType {
    AGENDA_PROPOSAL,
    AGENDA_REGULAR,
}

enum class AgendaProposalStatus {
    INTERNAL_ERROR,
    NOT_A_PROPOSAL,
    PROPOSAL_APPROVED,
    PROPOSAL_CANCELLED_BY_SUBMITTER,
    PROPOSAL_REJECTED,
    PROPOSAL_WAITING_FOR_APPROVAL,
}