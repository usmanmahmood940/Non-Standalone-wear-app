package com.example.nonstandalonewearos

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.nonstandalonewearos.ui.theme.NonStandaloneWearOSTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NonStandaloneWearOSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(onClick = {connectWearApp()})
                }
            }
        }
        Wearable.getMessageClient(this).addListener { messageEvent: MessageEvent? ->
            messageEvent?.let {
                Log.d(TAG, "Message path: ${it.path}")
                Log.d(TAG, "Message data: ${String(it.data)}")
                Toast.makeText(this, "Message: ${String(it.data)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun connectWearApp() {
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
                                .sendMessage(
                                    node.id,
                                    "/message_path",
                                    "Hello Wear Os from Usman".toByteArray()
                                )
                        }

                    }
                } else {
                    Log.d(TAG, "Capability request failed to return any results.")
                }
            }
        })

    }

    companion object {
        private const val CAPABILITY_WATCH_APP = "watch_client"
        private const val TAG = "MainActivity"

    }
}

@Composable
fun Greeting(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box {

        Button(
            modifier = modifier.align(alignment = Alignment.Center),
            onClick = onClick
        ) {
            Text(text = "Send Message", modifier = modifier)

        }
    }
}
