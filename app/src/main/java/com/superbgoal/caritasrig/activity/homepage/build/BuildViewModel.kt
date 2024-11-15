import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager

class BuildViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)
    var buildTitle by mutableStateOf(sharedPreferences.getString("buildTitle", "") ?: "")

    private val _buildData = MutableLiveData<Build?>()
    val buildData: LiveData<Build?> get() = _buildData

    private val _componentDetail = mutableStateOf<String?>(null)

    fun saveBuildTitle(title: String) {
        // Perbarui nilai buildTitle di ViewModel dan SharedPreferences
        buildTitle = title
        sharedPreferences.edit().putString("buildTitle", title).apply()

        // Ambil title dari kelas Build, jika tersedia, lalu set di BuildManager
        val buildTitleFromData = _buildData.value?.title ?: title
        BuildManager.setBuildTitle(buildTitleFromData)
        Log.d("BuildViewModell", "Build Title Saved: $title")
    }

    fun updateComponentDetail(build: Build) {
        build.components?.let { components ->
            val detail = buildString {
                components.processor?.let {
                    append("CPU: ${it.name}, ${it.core_count} cores, ${it.core_clock} GHz\n")
                }
                components.videoCard?.let {
                    append("GPU: ${it.name}, ${it.memory} GB\n")
                }
                // Tambahkan detail komponen lain di sini
            }
            _componentDetail.value = detail
            Log.d("BuildViewModel", "Component Detail Updated: $detail")
        } ?: run {
            _componentDetail.value = null
        }
    }

    fun setBuildData(build: Build) {
        _buildData.value = build

        // Perbarui buildTitle di ViewModel dan SharedPreferences
        buildTitle = build.title
        sharedPreferences.edit().putString("buildTitle", build.title).apply()

        // Set judul di BuildManager untuk konsistensi
        BuildManager.setBuildTitle(build.title)

        Log.d("BuildViewModel", "Build Data Updated with title: ${build.title}")
        updateComponentDetail(build) // Update komponen detail
    }
}
