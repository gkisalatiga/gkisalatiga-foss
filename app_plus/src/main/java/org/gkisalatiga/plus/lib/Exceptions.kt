/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.lib

class DownloadCancelledByClientException(errorMsg: String = "Cancelling the download process. The client refuses to continue the download buffer.") : Exception(errorMsg)
class FileNotDownloadableException(errorMsg: String = "The requested target remote file seems to be corrupt or cannot be downloaded directly.") : Exception(errorMsg)
class InvalidConnectionTestStringException(errorMsg: String = "Downloaded string does not match with the app's test string.") : Exception(errorMsg)
