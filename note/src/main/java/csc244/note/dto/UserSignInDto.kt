package csc244.note.dto

class UserSignInDto {
    var email: String? = null
    var password: String? = null
    var timeSpan: Int = 1
    var method: String = "authenticate"
    var timeUnit: String = "SECONDS"
}