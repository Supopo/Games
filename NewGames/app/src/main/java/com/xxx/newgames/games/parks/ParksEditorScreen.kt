package com.xxx.newgames.games.parks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.xxx.newgames.ui.components.GameActionChip
import com.xxx.newgames.ui.components.GameBackground
import com.xxx.newgames.ui.components.GameTopBar
import com.xxx.newgames.ui.components.GameUi

private val ParksAccent = Color(0xFFEC5F98)

@Composable
internal fun ParksEditorScreen(
    entries: List<String>,
    isSaving: Boolean,
    errorMessage: String?,
    onSave: (List<String>) -> Unit,
) {
    var draft by remember(entries) { mutableStateOf(entries.toList()) }
    var input by remember { mutableStateOf("") }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = ParksAccent,
        unfocusedBorderColor = Color.White.copy(alpha = .4f),
        focusedLabelColor = ParksAccent,
        unfocusedLabelColor = Color.White.copy(alpha = .7f),
        cursorColor = ParksAccent,
        disabledTextColor = Color.White.copy(alpha = .5f),
        disabledBorderColor = Color.White.copy(alpha = .2f),
        disabledLabelColor = Color.White.copy(alpha = .4f),
    )
    val addEntry: () -> Unit = {
        val entry = input.trim()
        if (entry.isNotEmpty()) {
            draft = draft + entry
            input = ""
        }
    }
    val saveAndBack: () -> Unit = { onSave(draft) }
    BackHandler(enabled = !isSaving, onBack = saveAndBack)

    GameBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding(),
        ) {
            GameTopBar(
                title = "添加公园",
                onBack = saveAndBack,
                backEnabled = !isSaving,
                titleInBar = true,
                actions = {
                    Text(draft.size.toString() + " 条", color = ParksAccent, fontSize = GameUi.actionSize)
                },
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    itemsIndexed(draft) { index, entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = (index + 1).toString() + ". " + entry,
                                color = Color.White,
                                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                            )
                            TextButton(
                                onClick = { draft = draft.filterIndexed { position, _ -> position != index } },
                                enabled = !isSaving,
                            ) { Text("删除", color = ParksAccent) }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = .12f))
                    }
                }
                errorMessage?.let {
                    Text(
                        it,
                        color = ParksAccent,
                        fontSize = GameUi.actionSize,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("输入公园名称") },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    maxLines = 3,
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addEntry() }),
                )
                GameActionChip(
                    text = "添加",
                    enabled = !isSaving,
                    onClick = addEntry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 24.dp),
                )
            }
        }
    }
}
