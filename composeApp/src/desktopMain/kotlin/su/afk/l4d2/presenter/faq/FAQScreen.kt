package su.afk.l4d2.presenter.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.faqAnswer1
import kotlinproject.composeapp.generated.resources.faqAnswer2
import kotlinproject.composeapp.generated.resources.faqAnswer3
import kotlinproject.composeapp.generated.resources.faqAnswer4
import kotlinproject.composeapp.generated.resources.faqAnswer5
import kotlinproject.composeapp.generated.resources.faqAnswer6
import kotlinproject.composeapp.generated.resources.faqAnswer7
import kotlinproject.composeapp.generated.resources.faqAnswer8
import kotlinproject.composeapp.generated.resources.faqQuestion1
import kotlinproject.composeapp.generated.resources.faqQuestion2
import kotlinproject.composeapp.generated.resources.faqQuestion3
import kotlinproject.composeapp.generated.resources.faqQuestion4
import kotlinproject.composeapp.generated.resources.faqQuestion5
import kotlinproject.composeapp.generated.resources.faqQuestion6
import kotlinproject.composeapp.generated.resources.faqQuestion7
import kotlinproject.composeapp.generated.resources.faqQuestion8
import org.jetbrains.compose.resources.stringResource


@Composable
fun FAQScreen() {
    val faqList = listOf(
        FAQItem(
            question = stringResource(Res.string.faqQuestion1),
            answer = stringResource(Res.string.faqAnswer1)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion2),
            answer = stringResource(Res.string.faqAnswer2)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion3),
            answer = stringResource(Res.string.faqAnswer3)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion4),
            answer = stringResource(Res.string.faqAnswer4)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion5),
            answer = stringResource(Res.string.faqAnswer5)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion6),
            answer = stringResource(Res.string.faqAnswer6)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion7),
            answer = stringResource(Res.string.faqAnswer7)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion8),
            answer = stringResource(Res.string.faqAnswer8)
        ),
    )

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(faqList) { faqItem ->
            FAQItemView(faqItem = faqItem)
        }
    }
}


data class FAQItem(
    val question: String,
    val answer: String
)

@Composable
fun FAQItemView(faqItem: FAQItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = faqItem.question,
//                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = faqItem.answer,
//                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}