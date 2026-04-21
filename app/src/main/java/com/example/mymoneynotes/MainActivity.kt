package com.example.mymoneynotes.com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mymoneynotes.ui.MyMoneyApp
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesTheme {
                MyMoneyApp()
            }
        }
    }
}