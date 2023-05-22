package it.polito.madgroup4.view.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LevelSelector(
    levels: List<String>,
    navController: NavController,
    setSelectedLevel: (String) -> Unit,
    unselectable: List<String> = listOf()
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
    )

    {
        items(levels.size) { index ->
            ElevatedCard(
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(enabled = !unselectable.contains(levels[index])) {
                                setSelectedLevel(levels[index])
                                navController.popBackStack()
                            }
                            .alpha(
                                if (unselectable.contains(levels[index])
                                ) 0.5f else 1f
                            ),
                    ) {
                        Text(
                            text = levels[index],
                            fontSize = 30.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}