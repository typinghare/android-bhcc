package csc244.note.common

object User {
    private var token: String = ""

    fun setToken(token: String) {
        this.token = token
    }
    
    fun getToken(): String {
        return token
    }
}