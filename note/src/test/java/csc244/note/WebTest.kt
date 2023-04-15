package csc244.note

import csc244.note.common.web.Listener
import csc244.note.dto.UserSignInDto
import org.junit.Test

class WebTest {
    @Test
    fun createListener() {
        val listener = Listener(UserSignInDto::class) { userDto ->
            println(userDto.email)
        }

        val userLoginDto = UserSignInDto()
        userLoginDto.email = "James Chan"

        listener.accept(userLoginDto)
    }
}