import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class HotPoint(
    var name: String? = "",
    var description: String? = ""
)