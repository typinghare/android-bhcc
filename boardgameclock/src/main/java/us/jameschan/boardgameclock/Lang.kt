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
            put("New Game", "新游戏")
            put("Settings", "设置")
            put("About", "关于")
            put("Clicking Sound Effect", "点击音效")
        }
    }

    private fun getJapanese(): Map<String, String> {
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
            put("New Game", "新游戏")
            put("Settings", "设置")
            put("About", "关于")
            put("Clicking Sound Effect", "点击音效")
            put("Warning Sound Effect", "警示音效")
            put("Warning Sound Effect", "警示音效")

            // Long sentences
            put(
                "The clock will emit a beep sound as a warning to players when there are only five minutes remaining.",
                "当时间仅剩五分钟时，计时器会鸣音示意棋手"
            )
            put("The clock will emit a beep sound whenever players tap the screen.", "棋手点击屏幕时，计时器鸣音示意")
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