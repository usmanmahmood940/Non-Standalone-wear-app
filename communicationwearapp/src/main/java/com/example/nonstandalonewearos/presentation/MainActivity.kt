/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.nonstandalonewearos.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.nonstandalonewearos.R
import com.example.nonstandalonewearos.presentation.theme.NonStandaloneWearOSTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp( onClick = { connectPhoneApp() })
        }
        Wearable.getMessageClient(this).addListener { messageEvent: MessageEvent? ->
            messageEvent?.let {
                Log.d(TAG, "Message path: ${it.path}")
                Log.d(TAG, "Message data: ${String(it.data)}")
                Toast.makeText(this, "Message: ${String(it.data)}", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun connectPhoneApp() {
        val capabilityInfoTask: Task<CapabilityInfo> = Wearable.getCapabilityClient(this)
            .getCapability(CAPABILITY_WATCH_APP, CapabilityClient.FILTER_REACHABLE)

        capabilityInfoTask.addOnCompleteListener(object : OnCompleteListener<CapabilityInfo?> {
            override fun onComplete(task: Task<CapabilityInfo?>) {
                if (task.isSuccessful()) {
                    val capabilityInfo: CapabilityInfo? = task.getResult()
                    val nodes = capabilityInfo?.nodes
                    nodes?.let {
                        for (node in nodes) {
                            Log.d(TAG, "Node: $node")
                            Wearable.getMessageClient(this@MainActivity)
                                .sendMessage(node.id, "/message_path", "Hello Usman from Wear OS".toByteArray())
                        }

                    }
                } else {
                    Log.d(TAG, "Capability request failed to return any results.")
                }
            }
        })
    }

    companion object{
        private const val CAPABILITY_WATCH_APP = "watch_server"
        private const val TAG = "MainActivity"
    }
}

@Composable
fun WearApp(greetingName: String="Android",onClick: () -> Unit) {
    NonStandaloneWearOSTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
            Button(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                onClick = onClick) {
                Text(text = "Send Message")
            }
        }
    }
}

@Composable
fun Greeting(greetingName: String){
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}
