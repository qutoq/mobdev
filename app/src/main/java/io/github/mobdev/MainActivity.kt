package io.github.mobdev

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ContactApp()
                }
            }
        }
    }
}

@Composable
fun ContactApp() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasPermission = isGranted }

    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!hasPermission) {
                Button(
                    onClick = { launcher.launch(Manifest.permission.READ_CONTACTS) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Запросить разрешение")
                }
                Text("Разрешение не получено", modifier = Modifier.padding(16.dp))
            } else {
                if (selectedContact == null) {
                    val contacts: List<Contact> = remember { context.fetchAllContacts() }

                    LazyColumn {
                        items(contacts) { contact: Contact ->
                            Text(
                                text = contact.name ?: "Unknown",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedContact = contact }
                                    .padding(16.dp)
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Имя: ${selectedContact?.name ?: "—"}")
                        Text("Телефон: ${selectedContact?.phoneNumber ?: "—"}")
                        Text("Email: ${selectedContact?.email ?: "—"}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { selectedContact = null }) {
                            Text("Назад")
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("Range")
fun Context.fetchAllContacts(): List<Contact> {
    val list = mutableListOf<Contact>()
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val name = cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val rawEmail = cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
            val email = if (rawEmail == null || rawEmail == phoneNumber) "не указан" else rawEmail

            list.add(Contact(name, phoneNumber, email))
        }
    }
    return list
}