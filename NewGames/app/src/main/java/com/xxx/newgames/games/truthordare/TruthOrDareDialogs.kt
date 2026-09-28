package com.xxx.newgames.games.truthordare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxx.newgames.ui.components.GameTopBar
import com.xxx.newgames.ui.components.GameUi

private val ResultYellow = Color(0xFFEEE685)
private val ResultGreen = Color(0xFF76EEC6)

@Composable
internal fun TruthOrDareResultDialog(
    state: TruthOrDareUiState,
    onChoose: (TruthOrDareChoice) -> Unit,
    onChangePrompt: () -> Unit,
    onRefuse: () -> Unit,
    onDismiss: () -> Unit,
) {
    val stage = state.resultStage ?: return
    val changePromptInteractionSource = remember { MutableInteractionSource() }
    val sink = remember { MutableInteractionSource() }
    // 使用页内浮层，避免系统 Dialog 在返回后吞掉首页第一次点击
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .45f))
            .clickable(interactionSource = sink, indication = null, onClick = {}),
        contentAlignment = Alignment.Center,
    ) {
        Column(modifier = Modifier.width(250.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.linearGradient(listOf(TruthOrDarePurple, Color(0xFF2B2B2B))),
                        RoundedCornerShape(20.dp),
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = when (stage) {
                        TruthOrDareResultStage.CHOOSE -> "真心话大冒险"
                        TruthOrDareResultStage.QUESTION -> state.choice?.title.orEmpty()
                        TruthOrDareResultStage.REFUSED -> "拒绝游戏"
                    },
                    color = ResultYellow,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center,
                ) {
                    when (stage) {
                        TruthOrDareResultStage.CHOOSE -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("本轮选中的玩家是", color = Color.White, fontSize = 16.sp)
                                Text(
                                    text = state.currentPlayer,
                                    color = ResultYellow,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 30.dp),
                                )
                                Text("你可以选择", color = Color.White, fontSize = 16.sp)
                            }
                        }
                        TruthOrDareResultStage.QUESTION -> {
                            Text(
                                text = state.prompt.orEmpty(),
                                color = Color.White,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                        TruthOrDareResultStage.REFUSED -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "自己的选择，跪着也要完成！",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    "喝酒酒吧",
                                    color = ResultYellow,
                                    fontSize = 30.sp,
                                    modifier = Modifier.padding(vertical = 30.dp),
                                )
                            }
                        }
                    }
                }
                if (stage == TruthOrDareResultStage.QUESTION) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "换一题",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable(
                                interactionSource = changePromptInteractionSource,
                                indication = null,
                                role = Role.Button,
                                onClick = onChangePrompt,
                            )
                            .padding(8.dp),
                    )
                }
            }
            Spacer(Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (stage == TruthOrDareResultStage.REFUSED) {
                    Arrangement.Center
                } else {
                    Arrangement.SpaceBetween
                },
            ) {
                when (stage) {
                    TruthOrDareResultStage.CHOOSE -> {
                        ResultButton("大冒险", TruthOrDarePink) { onChoose(TruthOrDareChoice.DARE) }
                        ResultButton("真心话", ResultGreen) { onChoose(TruthOrDareChoice.TRUTH) }
                    }
                    TruthOrDareResultStage.QUESTION -> {
                        ResultButton("拒绝", TruthOrDarePink, onRefuse)
                        ResultButton("同意", ResultGreen, onDismiss)
                    }
                    TruthOrDareResultStage.REFUSED -> ResultButton("确定", ResultGreen, onDismiss)
                }
            }
        }
    }
}

@Composable
private fun ResultButton(text: String, borderColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(15.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .width(100.dp)
            .height(GameUi.primaryButtonHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = Color.White, fontSize = GameUi.primaryButtonSize)
    }
}

@Composable
internal fun TruthOrDareEditorScreen(
    editor: TruthOrDareEditor,
    entries: List<String>,
    isSaving: Boolean,
    errorMessage: String?,
    onSave: (List<String>) -> Unit,
) {
    var draft by remember(editor, entries) { mutableStateOf(entries.toList()) }
    var input by remember(editor) { mutableStateOf("") }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = TruthOrDarePink,
        unfocusedBorderColor = Color.White.copy(alpha = .4f),
        focusedLabelColor = TruthOrDarePink,
        unfocusedLabelColor = Color.White.copy(alpha = .7f),
        cursorColor = TruthOrDarePink,
        disabledTextColor = Color.White.copy(alpha = .5f),
        disabledBorderColor = Color.White.copy(alpha = .2f),
        disabledLabelColor = Color.White.copy(alpha = .4f),
    )
    val addEntry: () -> Unit = {
        val entry = input.trim().ifEmpty {
            if (editor == TruthOrDareEditor.PLAYERS) {
                generateSequence(1) { it + 1 }
                    .map { "${it}号玩家" }
                    .first { it !in draft }
            } else ""
        }
        if (entry.isNotEmpty()) {
            draft = draft + entry
            input = ""
        }
    }
    val saveAndBack: () -> Unit = { onSave(draft) }
    BackHandler(enabled = !isSaving, onBack = saveAndBack)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(TruthOrDarePurple, TruthOrDareDark, TruthOrDareDark)),
            )
            .navigationBarsPadding()
            .statusBarsPadding(),
    ) {
        GameTopBar(
            title = editor.title,
            onBack = saveAndBack,
            backEnabled = !isSaving,
            titleInBar = true,
            actions = {
                Text(draft.size.toString() + " 条", color = TruthOrDarePink, fontSize = GameUi.actionSize)
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
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = (index + 1).toString() + ". " + entry,
                            color = Color.White,
                            modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                        )
                        TextButton(
                            onClick = { draft = draft.filterIndexed { position, _ -> position != index } },
                            enabled = !isSaving,
                        ) { Text("删除", color = TruthOrDarePink) }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = .12f))
                }
            }
            errorMessage?.let {
                Text(
                    it,
                    color = TruthOrDarePink,
                    fontSize = GameUi.actionSize,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(if (editor == TruthOrDareEditor.PLAYERS) "玩家姓名" else "输入题目") },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                maxLines = 3,
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { addEntry() }),
            )
            TruthOrDareAction(
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
