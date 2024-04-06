package ui.push

import java.io.PrintWriter

class PushUtil {

    fun pushException(push: PrintWriter, msg: String) {
        push.println("${Keywords.WAS_EXCEPTION} $msg")
    }

    object Keywords {
        const val WAS_EXCEPTION = "exception:"
    }

}