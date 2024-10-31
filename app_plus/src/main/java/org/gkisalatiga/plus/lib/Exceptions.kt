package org.gkisalatiga.plus.lib

class InvalidConnectionTestStringException(errorMsg: String = "Downloaded string does not match with the app's test string.") : Exception(errorMsg)