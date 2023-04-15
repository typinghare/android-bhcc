package csc244.note.dto

open class UserSignInResponseDto {
    var token: String? = null
    var owner: String? = null
    var ttl: Int? = null
    var creationDate: Long? = null
}