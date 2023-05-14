package us.jameschan.boardgameclock

object Lang {
    private val languages: MutableMap<String, Map<String, String>> = mutableMapOf()

    init {
        languages["chinese"] = getChinese()
        languages["japanese"] = getJapanese()
    }

    private fun getChinese(): Map<String, String> {
        return HashMap<String, String>().apply {
            put("username:", "用户名：")
            put("password:", "密码：")
            put("Welcome to sign up!", "欢迎创建新用户！")
            put("Please input username and password.", "请输入用户名和密码")
            put("Signing up...", "注册中...")
            put("Signing in...", "登录中...")
            put("Loading settings...", "正在加载设置...")
            put("Sign In", "登录")
            put("Sign Up", "注册")
            put("Create New Account", "创建账号")
        }
    }

    private fun getJapanese(): Map<String, String> {
        return HashMap<String, String>().apply {
            put("", "")
        }
    }

    private fun translate(lang: String, content: String): String {
        if (lang == "english") return content

        val language = languages[lang] ?: return ""

        return language[content.trim()] ?: ""
    }

    fun translate(content: String): String {
        val lang = LocalUser.language
        return translate(lang, content)
    }
}